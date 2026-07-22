(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Saint Lucia procurement law, whether a claimed engagement
  fee actually equals base + months x rate, whether the engagement's
  own declared suspension under the Public Procurement Act, Cap. 15.10
  s.114 still disqualifies it, whether a tax-registration record has
  been verified for a filing that requires it, or when a draft stops
  being a draft and becomes a real-world Central Public Procurement
  Board portal submission, so this MUST be a separate system able to
  *reject* a proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Six checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Suspension still
       disqualifying                -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own declared
                                       suspension (Public Procurement
                                       Act, Cap. 15.10 s.114) STILL
                                       disqualifies it as of its own
                                       declared `:submission-date`
                                       (Public Procurement Regulations
                                       2023 reg.25(2)(b)), and HARD-hold
                                       if so. FLAGSHIP genuinely new
                                       check for the iso3166 family (grep-
                                       verified absent as a governor
                                       check function name fleet-wide at
                                       build time) -- a VARIABLE-duration
                                       (6-60 month, statutorily bounded)
                                       disqualification-window recompute,
                                       distinct from every prior sibling's
                                       fixed-constant date shape.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. Tax registration unverified  -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-tax-registration?
                                       true`, INDEPENDENTLY check
                                       `:tax-registration-verified?`.
                                       CONDITIONAL on the engagement's
                                       own ground truth. Grounded in the
                                       Company Tax Registration the
                                       Inland Revenue Department
                                       performs as a SEPARATE, subsequent
                                       act to ROCIP business
                                       registration.
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(ROCIP登録/税務登録確認/停止処分不在確認等)が充足していない状態での提案"}]))))

(defn- suspension-disqualifying-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own declared suspension (Public Procurement Act, Cap.
  15.10 s.114) STILL disqualifies it as of its own declared
  `:submission-date` (Public Procurement Regulations 2023 reg.25(2)(b)),
  and HARD-hold if so -- the flagship genuinely new check this vertical
  adds. Evaluated for EVERY `:filing/submit`, unconditionally (not
  gated behind a `:requires-X?` engagement flag), the same 'always
  applies' shape Bulgaria's tax-arrears check and Azerbaijan's/
  Armenia's registry-membership checks use."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/suspension-disqualifying? e)
        [{:rule :suspension-disqualifying
          :detail (str subject " の停止処分(開始日=" (:suspension-start-date e)
                      ", 期間=" (:suspension-duration-months e) "ヶ月)が申告提出日("
                      (:submission-date e)
                      ")時点でなお有効(s.114, Regulations 2023 reg.25(2)(b)) -- 提出提案は進められない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- tax-registration-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-tax-registration? true`, INDEPENDENTLY check
  `:tax-registration-verified?` -- CONDITIONAL on the engagement's own
  ground truth. Grounded in the Company Tax Registration the Inland
  Revenue Department performs as a separate, subsequent act to ROCIP
  business registration."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-tax-registration? e))
                 (not (true? (:tax-registration-verified? e))))
        [{:rule :tax-registration-unverified
          :detail (str subject " は税務登録(Inland Revenue Department, Company Tax Registration)確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (suspension-disqualifying-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (tax-registration-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
