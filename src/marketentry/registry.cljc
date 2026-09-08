(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `lawful-suspension-duration?` / `compute-suspension-end` /
  `suspension-disqualifying?` are THIS vertical's own new ground-truth
  recompute, grounding LCA's flagship governor check
  (`marketentry.governor/suspension-disqualifying-violations`): the
  Public Procurement Act, Cap. 15.10 s.114 (own primary text, see
  `marketentry.facts`) lets the Director suspend a tenderer or
  contractor for 'a minimum period of 6 months and a maximum period of
  5 years' (s.114(3)), and the Public Procurement Regulations, 2023
  reg.25(2)(b) disqualifies a suspended tenderer 'for the duration of
  the period of suspension'.

  This is a DIFFERENT check SHAPE from every prior sibling this repo's
  family has implemented, and from every prior DATE-shaped check
  specifically: Barbados's Suppliers Register validity window and
  Grenada's director-conviction lookback window are each anchored to a
  single FIXED statutory constant (3 years / 2 years). Saint Lucia's
  own s.114(3) instead gives the Director a RANGE (6-60 months) to set
  an actual suspension duration within -- so the independent recompute
  here has TWO legs, not one: (1) is the declared duration itself
  LAWFUL (within the statutory 6-60-month bound -- a data-integrity
  check on the record, not merely a date computation), and only if so
  (2) does the computed suspension window [start, start+duration] still
  cover the declared submission date. An engagement that declares an
  out-of-range duration is treated as an invalid/untrustworthy
  suspension record and is conservatively held pending correction,
  exactly like an unverified compliance claim -- this catalog does not
  assume a self-reported duration is lawful just because it is present.

  Dates are plain ISO-8601 \"YYYY-MM-DD\" strings -- deliberately no
  external date/calendar library and no host date API (`java.time` /
  `js/Date`), the same technique this family's Barbados and Grenada
  siblings established for their own (single-unit, year-based) date
  checks, extended here to MONTH-granularity arithmetic with year
  rollover (a genuinely more involved computation: adding N months,
  not a fixed number of years, requires carrying the overflow into the
  year component). `compute-suspension-end` bumps the year/month pair
  by converting to a zero-based total-month index, adding the
  duration, and re-deriving year/month -- a calendar approximation that
  keeps the day-of-month component unchanged (the same simplification
  this family's fixed-year-bump checks already make for leap-year/
  month-length edges), compared with plain string `compare`, which
  sorts zero-padded ISO-8601 dates in chronological order.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real Central Public Procurement Board system. It builds
  the RECORD an operator would keep, not the act of submitting a portal
  registration itself (that is `marketentry.operation`'s
  `:filing/submit`, always human-gated -- see README Actuation)."
  (:require [kotoba.lang.text :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(def ^:private money-scale
  "Sub-minor-unit scale used when comparing two money amounts: 1/10000 of
  a unit. Coarser than double representation error by many orders of
  magnitude, finer than any real currency's minor unit (2 decimals for
  most, 3 for KWD/BHD/OMR, 0 for JPY/KRW)."
  10000)

(defn- money=
  "Exact-at-money-precision equality for two amounts.

  `==` on raw doubles is NOT the right comparison for money. With
  whole-unit fees the two agree, but as soon as an amount carries
  cents the sum `base + rate x months` is routinely not the double
  nearest the true total, and a CORRECT claim compares false: measured
  on this exact shape, 40,989 of 327,060 cent-denominated combinations
  (12.5%) were rejected while being right, against 0 of 327,060 in
  whole units.

  Rounding both sides to `money-scale` before comparing removes the
  representation error while preserving every distinction money can
  actually carry."
  [x y]
  (and (number? x) (number? y)
       (= (Math/round (* money-scale (double x)))
          (Math/round (* money-scale (double y))))))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  ;; nil when any field is not a number: an un-recomputable engagement is
  ;; un-verifiable, which is neither `correct` nor a ClassCastException
  ;; thrown out of the caller.
  (when (and (number? base-fee) (number? monthly-rate) (number? monitoring-months))
    (+ (double base-fee)
       (* (double monthly-rate) (double monitoring-months)))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (money= claimed-fee (compute-engagement-fee engagement)))

(def min-suspension-months
  "Public Procurement Act, Cap. 15.10 s.114(3)'s own statutory floor:
  'a suspension under this section is for a minimum period of 6
  months'."
  6)

(def max-suspension-months
  "Public Procurement Act, Cap. 15.10 s.114(3)'s own statutory ceiling:
  '... and a maximum period of 5 years' (60 months)."
  60)

(defn lawful-suspension-duration?
  "Does `duration-months` fall within s.114(3)'s own statutory range
  (6-60 months inclusive)? A nil duration is never lawful here -- a
  suspension record must declare a duration to be trusted."
  [duration-months]
  (boolean
   (and (number? duration-months)
        (<= min-suspension-months duration-months max-suspension-months))))

(defn compute-suspension-end
  "The ground-truth date on which a suspension starting on `start-date`
  (\"YYYY-MM-DD\") for `duration-months` STOPS disqualifying a tenderer
  or contractor under s.114(3) -- `duration-months` calendar months
  later, same day-of-month. Returns nil if either input is missing."
  [start-date duration-months]
  (when (and start-date duration-months (>= (count start-date) 7))
    (let [year (#?(:clj Integer/parseInt :cljs js/parseInt) (subs start-date 0 4))
          month (#?(:clj Integer/parseInt :cljs js/parseInt) (subs start-date 5 7))
          day+rest (subs start-date 7)
          total-months (+ (* year 12) (dec month) (long duration-months))
          new-year (quot total-months 12)
          new-month (inc (mod total-months 12))]
      (str (zero-pad new-year 4) "-" (zero-pad new-month 2) day+rest))))

(defn suspension-disqualifying?
  "Does `engagement`'s own declared suspension STILL disqualify it under
  s.114/reg.25(2)(b) as of its own declared `:submission-date`? Two
  legs, independently recomputed:

    1. an OUT-OF-RANGE `:suspension-duration-months` (violating
       s.114(3)'s own 6-60-month bound) is treated as an untrustworthy
       record -- conservatively disqualifying pending correction; and
    2. otherwise, does `submission-date` fall ON OR AFTER
       `suspension-start-date` and ON OR BEFORE the computed
       `suspension-start-date + duration` (reg.25(2)(b): disqualified
       'for the duration of the period of suspension')?

  No `:suspension-start-date` on file -> never disqualifying (no
  suspension recorded for this tenderer/contractor or its named
  directors/shareholders/staff)."
  [{:keys [suspension-start-date suspension-duration-months submission-date]}]
  (boolean
   (when suspension-start-date
     (or (not (lawful-suspension-duration? suspension-duration-months))
         (when-let [end (compute-suspension-end suspension-start-date suspension-duration-months)]
           (and submission-date
                (<= (compare suspension-start-date submission-date) 0)
                (<= (compare submission-date end) 0)))))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
