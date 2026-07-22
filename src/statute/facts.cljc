(ns statute.facts
  "General-law compliance catalog for Saint Lucia (LCA) -- extends this
  repo's existing `marketentry.facts` (public-procurement market-entry
  only, narrow scope) with a second, orthogonal catalog of statutes a
  company operating in this jurisdiction must generally track for
  compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/
  -arm/-atg/-brb/-dma/-grd's `statute.facts` (ADR-2607141700,
  cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL Saint Lucia government-hosted URL --
  never fabricated. Saint Lucia's official consolidated-law host is
  `attorneygeneralchambers.com/laws-of-saint-lucia/`, a full paginated
  'Revised Laws of Saint Lucia (2023)' law browser run by the Attorney
  General's Chambers (2nd Floor Francis Compton Building, Waterfront,
  Castries) -- reached this session via link-following from
  `www.govt.lc`'s own footer, not a guessable top-level domain (the
  task's own suggested candidate, `slucode.saintlucia.gov.lc`, does NOT
  exist -- `NXDOMAIN` on direct `nslookup`, confirmed absent rather
  than merely unreachable). Every entry below was fetched directly this
  session (HTML via a tag-stripped dump; the finance.gov.lc-hosted PDF
  of the Labour Act carried a genuine text layer and needed no OCR,
  unlike the scanned-image PDFs this loop's Dominica iteration hit):

  - Companies Act (Acts 19 of 1996, 10 of 1997, 9 of 1998, 7 of 1999,
    33 of 1999, 10 of 2001, 21 of 2006, 13 of 2015, 6 of 2016, 10 of
    2018, 4 of 2021, 9 of 2023 and S.I. 120/2014) -- confirmed via its
    own text (`attorneygeneralchambers.com/laws-of-saint-lucia/
    companies-act/section-8`): '8. Certificate of incorporation. Upon
    receipt of the articles of the incorporation, the Registrar shall
    issue a certificate of incorporation in accordance with section
    511; and the certificate is conclusive proof of the incorporation
    of the company named in the certificate' -- the SAME section-8-
    certificate shape this catalog's ATG/DMA/GRD siblings each
    independently document for their own OECS-model Companies Acts,
    consistent with all four being of a shared lineage. Act 19 of 1996
    itself came into force 1 January 1997 (S.I. 62/1996). The
    Registrar's functions are exercised through the Registry of
    Companies and Intellectual Property (ROCIP), established by a
    SEPARATE Act -- the Companies and Intellectual Property Registry
    Act (Acts 12 of 2000 and 10 of 2020, in force 1 April 2000) -- see
    `marketentry.facts` for the full business-registration/tax
    two-act finding.
  - Labour Act (Acts 37 of 2006, 6 of 2011 and 5 of 2020) -- confirmed
    via its own text on BOTH `attorneygeneralchambers.com/laws-of-
    saint-lucia/labour-act/act` (citation metadata: 'Act 37 of 2006 ..
    in force 1 August 2012 (S.I. 70/2012)', amended by Act 6 of 2011
    in force 3 May 2011 and Act 5 of 2020 in force 17 June 2020) AND a
    directly-downloaded `pdftotext`-read copy hosted at `www.govt.lc`
    (whose own s.1(1) cites itself internally as 'the Labour Code
    2006' -- the SAME Act under two names, cross-validated by
    identical section numbering and content on both portals). Division
    10, 'Termination of Employment' (ss.128-159: s.129 valid reason for
    dismissal, s.131 unfair dismissal, s.132 constructive dismissal,
    s.133 summary dismissal for serious misconduct, s.145 termination
    due to redundancy, s.156 certificate of termination) and Division
    11, 'Termination Benefits' (ss.160+: redundancy pay, severance
    pay) -- a single consolidated employment code, the same shape
    ATG's own Labour Code (CAP. 27) and GRD's Employment Act (Cap. 89)
    use, rather than DMA's two-separate-statutes split.
  - Data Protection Act (Acts 11 of 2011 and 2 of 2015, and S.I.
    4/2023) -- confirmed via its own text
    (`attorneygeneralchambers.com/laws-of-saint-lucia/data-protection-
    act/act`): Part 2 'APPOINTMENT, FUNCTIONS AND POWERS OF THE DATA
    PROTECTION COMMISSIONER' (ss.5-24: appointment, tenure,
    independence, powers of investigation, entry and search), Part 3
    'OBLIGATION ON DATA CONTROLLERS', Part 4 'REGISTRATION OF DATA
    PROTECTION CONTROLLERS'. HONEST NOTE, unlike GRD's simpler
    'Data Protection Act, 2023' finding: this Act was enacted in 2011
    but its own citation states it is only PARTIALLY in force -- 'Act
    11 of 2011 .. in force 31 January 2023 Part I, sections 32 to 43
    of Part III, Part VI and paragraphs (a) to (g) of Schedule 2 (S.I.
    4/2023)' -- meaning the bulk of the Act sat without a commencement
    order for roughly twelve years before S.I. 4/2023 gave the listed
    parts operative effect; this catalog does NOT claim the whole Act
    is in force, only the parts the commencement instrument names.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.

  See `marketentry.facts` for this session's separate, honest finding
  on Saint Lucia's International Business Companies Act (Act 40 of
  1999, as amended through Act 2 of 2022) and the FSRA's three
  international-sector Acts -- documented there rather than duplicated
  in this general-compliance catalog, since IBC status is an elective
  regime a company opts into, not a generally-applicable law every
  Saint Lucia company must track.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"LCA"
   [{:statute/id "lca.companies-act"
     :statute/title "Companies Act"
     :statute/jurisdiction "LCA"
     :statute/kind :law
     :statute/law-number "Acts 19 of 1996, 10 of 1997, 9 of 1998, 7 of 1999, 33 of 1999, 10 of 2001, 21 of 2006, 13 of 2015, 6 of 2016, 10 of 2018, 4 of 2021, 9 of 2023 and S.I. 120/2014 (Act 19 of 1996 in force 1 January 1997, S.I. 62/1996)"
     :statute/url "https://attorneygeneralchambers.com/laws-of-saint-lucia/companies-act/section-8"
     :statute/url-provenance :official-attorneygeneralchambers
     :statute/enacted-date "1997-01-01"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "lca.labour-act"
     :statute/title "Labour Act"
     :statute/jurisdiction "LCA"
     :statute/kind :law
     :statute/law-number "Acts 37 of 2006, 6 of 2011 and 5 of 2020 (Act 37 of 2006 in force 1 August 2012, S.I. 70/2012; cited internally in its own s.1(1) as 'the Labour Code 2006')"
     :statute/url "https://attorneygeneralchambers.com/laws-of-saint-lucia/labour-act/act"
     :statute/url-provenance :official-attorneygeneralchambers
     :statute/enacted-date "2012-08-01"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:labor :employment :termination}}
    {:statute/id "lca.data-protection-act"
     :statute/title "Data Protection Act"
     :statute/jurisdiction "LCA"
     :statute/kind :law
     :statute/law-number "Acts 11 of 2011 and 2 of 2015 (Part I, ss.32-43 of Part III, Part VI and Schedule 2(a)-(g) in force 31 January 2023 per S.I. 4/2023 -- PARTIAL commencement, see namespace docstring)"
     :statute/url "https://attorneygeneralchambers.com/laws-of-saint-lucia/data-protection-act/act"
     :statute/url-provenance :official-attorneygeneralchambers
     :statute/enacted-date "2023-01-31"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:data-protection :privacy}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-lca statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "LCA")) " LCA statutes seeded with an "
                 "official government-hosted citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
