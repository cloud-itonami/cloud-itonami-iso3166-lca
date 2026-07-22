# ADR-0001: Architecture — Saint Lucia market-entry compliance actor (`marketentry`)

**Status**: accepted
**Date**: 2026-07-23

## Context

`cloud-itonami-iso3166-lca` was published as a `:blueprint` (docs +
`blueprint.edn` + `deps.edn`, then a country-level `culture.facts`
catalog in a separate Wave 1 batch) but carried ZERO `src/marketentry`
or `src/statute` content -- its `:public-sector/market-entry-
compliance` domain, declared in `blueprint.edn`, was unimplemented, and
`CONTRIBUTING.md`/`GOVERNANCE.md` still carried leftover template text
for a different country (`cloud-itonami-iso3166-khm` / Cambodia). This
ADR closes both gaps, following the pattern established by
`cloud-itonami-iso3166-jpn` (origin) and the OECS/Eastern-Caribbean
siblings `cloud-itonami-iso3166-atg` (Antigua and Barbuda),
`cloud-itonami-iso3166-dma` (Dominica), `cloud-itonami-iso3166-grd`
(Grenada) and `cloud-itonami-iso3166-est` (Estonia, studied for
contrast as a non-OECS sibling) -- the simpler, no-`goyoukiki` shape
this blueprint also uses (`blueprint.edn`'s `:required-technologies`
does not list `:ontology`).

## Decision

Build the full governed-actor architecture for `marketentry`, mirroring
JPN/ATG/DMA/GRD's harness verbatim (StateGraph node names, governor
hard/escalate contract, phase 0-3 rollout, `Store` protocol with
MemStore + DatomicStore parity) and researching Saint Lucia's own real
market-entry rules from scratch for the country-specific content.

- **Store**: `marketentry.store`, MemStore + DatomicStore, proven parity
  via contract test.
- **Registry**: `marketentry.registry`, pure DRAFT-certificate
  construction via `unsigned-certificate`, jurisdiction-scoped sequence
  numbering (`LCA-DFT-000000`, `LCA-SUB-000000`), plus the flagship
  suspension-window date recompute (see below).
- **Governor**: `:market-entry-compliance-governor` (family keyword from
  `blueprint.edn`).
- **Entity shape**: `engagement`, sequential draft -> submit on the same
  record. `high-stakes` = `#{:actuation/draft-filing
  :actuation/submit-filing}`.
- **Phase**: 0->3; `:filing/draft` and `:filing/submit` NEVER auto-
  commit at any phase.

### Discovery path: `slucode.saintlucia.gov.lc` does not exist; the real portal is reached via `www.govt.lc`

The task's own suggested candidate host, `slucode.saintlucia.gov.lc`,
was checked directly (`nslookup`) and confirmed absent -- `NXDOMAIN`,
not merely unreachable. The real discovery path was Saint Lucia's
official web portal, `www.govt.lc` (HTTP 200 with a standard curl
User-Agent), whose own footer links `attorneygeneralchambers.com`, the
Attorney General's Chambers' site, which itself hosts a full paginated
"Revised Laws of Saint Lucia (2023)" law browser at
`attorneygeneralchambers.com/laws-of-saint-lucia/` (8 pages, ~2000+
named Acts) -- the functional equivalent of the other OECS siblings'
`laws.gov.gd` / `laws.gov.ag` consolidated-law portals. Separately,
`www.finance.gov.lc` (the Ministry of Finance's own site; a bare `curl
-A Mozilla/5.0` without an `Accept` header got HTTP 406 from this
specific host's WAF, resolved by sending a fuller browser-style header
set) hosts its own live "/tenders/" section and a "Legal Instruments"
document library with the Public Procurement Act, its amendments and
its 2023 Regulations as directly-downloadable PDFs.

### Which body administers procurement, and the flagship HARD check: `suspension-disqualifying` — a variable-duration window, not a fixed constant

The Public Procurement Act, Cap. 15.10 (Acts 19 of 2015 and 13 of 2020;
Act 19 of 2015 in force 1 June 2021 per S.I. 202/2020) establishes a
Director of Public Procurement (Ministry responsible for finance,
ss.5-10) and a Central Public Procurement Board (s.11, jurisdiction
limited to procurement of "major value" -- $500,000 and above, Schedule
1 -- per s.12(1)). An Extraordinary Gazette dated 29 July 2024,
downloaded directly, confirms the Board is genuinely staffed (members
appointed for a 1 June 2024 - 31 May 2027 term under ss.11-22 and
s.14(2)) -- real operational evidence, not merely a paper provision.

s.114 ("Suspension of tenderer and contractor"), cross-read from both
the Cap.15.10 PDF (`finance.gov.lc/resources/download/2189`) and the AG
Chambers' own web edition (`attorneygeneralchambers.com/laws-of-saint-
lucia/public-procurement-act/section-114`, verbatim-identical text),
lets the Director suspend a tenderer/contractor on nine grounds
including a criminal conviction relating to obtaining or attempting to
obtain a procurement contract. Critically, s.114(3) sets a STATUTORY
RANGE, not a fixed constant: "a suspension under this section is for a
minimum period of 6 months and a maximum period of 5 years." s.114(4)
extends the suspension to "named directors, shareholders or staff ...
where the investigation demonstrates the involvement of such persons"
-- the finding `rep-spec-basis` is grounded in. The Public Procurement
Regulations, 2023 (Statutory Instrument, 2023, No. 133, gazetted 10
October 2023, downloaded directly and read via `pdftotext`) make the
disqualification link explicit: reg.25(2)(b) disqualifies a tenderer
"suspended under section 114 of the Act, for the duration of the period
of suspension."

`marketentry.registry/suspension-disqualifying?` independently
recomputes this with TWO legs, not one: (1) is the engagement's own
declared `:suspension-duration-months` itself LAWFUL under s.114(3)'s
6-60-month bound (an out-of-range duration is treated as an
untrustworthy record and is conservatively disqualifying regardless of
the apparent window); and only if so, (2) does the computed window
`[suspension-start-date, suspension-start-date + duration]` still cover
the engagement's own declared `:submission-date`. This is a genuinely
different check SHAPE than every prior iso3166 sibling: Bulgaria's ЗОП
Art. 54(5) de-minimis is a PERCENTAGE-OF-TURNOVER formula, Albania's
Neni 76(2)(c) carve-out is a FLAT STATUTORY CONSTANT, Azerbaijan's/
Armenia's flagship checks are plain BOOLEAN registry-membership reads,
Antigua and Barbuda's is a discrete 3-TIER THRESHOLD classification,
Dominica's is a DUAL independently-escalating AUTHORITY ladder, and
Barbados's/Grenada's own DATE-recompute checks are each anchored to a
SINGLE FIXED constant (3 years / 2 years) in opposite polarities.
Saint Lucia's own s.114(3) is neither: it is a VARIABLE-duration window
whose own bounds are themselves statutorily constrained, requiring the
governor to validate the record's internal legality in addition to the
date arithmetic -- a sixth distinct check shape for the family, reached
by reading Saint Lucia's own s.114 text rather than copied from any
sibling's citation. `compute-suspension-end` also required genuinely
more involved date arithmetic than any prior sibling's fixed-year bump:
adding an arbitrary MONTH count (not a fixed number of years) requires
carrying overflow into the year component, implemented here as a
zero-based total-month-index conversion with no external date library,
consistent with this family's existing "plain ISO-8601 string
arithmetic" discipline.

### The one-act-vs-two-acts business-registration/tax question

Confirmed as a clean TWO-ACT model, the same shape ATG's ABIPCO/IRD and
GRD's CAIPO/IRD findings document: `www.govt.lc`'s own "Businesses"
services page lists a "Company Name Search and Reservation Form" (ROCIP
side) and separately a "Company Tax Registration form", whose own page
states it "is used by non-individual enterprises for registering with
the Inland Revenue department" -- a separate, subsequent act. Business
registration itself is performed by the Registry of Companies and
Intellectual Property (ROCIP), a department of the Attorney General's
Chambers established by the Companies and Intellectual Property
Registry Act (Acts 12 of 2000 and 10 of 2020, in force 1 April 2000) --
itself a SEPARATE Act from the substantive Companies Act (Acts 19 of
1996 through 9 of 2023, in force 1 January 1997) it administers, the
same two-Act-for-one-registry shape Grenada's CAIPO Act/Companies Act
split documents. The Companies Act's own s.8 ("Certificate of
incorporation ... conclusive proof of the incorporation of the company
named in the certificate"), read directly from the AG Chambers' web
edition, matches the same section-8-certificate shape ATG/DMA/GRD each
independently document for their own OECS-model Companies Acts.

**Honest scope-narrowing on terminology**: unlike GRD's/ATG's own
confirmed "Taxpayer Identification Number (TIN)" terminology, this
iteration could NOT independently confirm that Saint Lucia's Inland
Revenue Department uses the specific term "TIN" -- `irdstlucia.gov.lc`
(the domain the AG Chambers' own "Quick Links" cite for IRD) resolved
this session to what its own markup identifies as a generic "coming
soon" placeholder, and the IRD's own 2010 brochure (downloaded and read
directly) does not use the word "TIN" in the extracted text. This ADR
and the catalog therefore cite the confirmed mechanism -- "Company Tax
Registration" -- and use `:requires-tax-registration?`/
`:tax-registration-verified?` engagement fields rather than borrowing
`:requires-tin?`/`:tin-verified?` from a sibling's jurisdiction.

### International Business Companies Act — checked, found real, deliberately kept out of scope

Saint Lucia genuinely has an International Business Companies Act (Act
40 of 1999, amended twelve times through Act 2 of 2022), with its own
registration track (Part 2, own s.6 "Certificate of incorporation" --
a DIFFERENT section number than the general Companies Act's own s.8),
registered-agent requirement (Part 4) and a "PART 1A MONITORING OF
COMPLIANCE" reflecting the global 2015-2019-era BEPS/economic-substance
tightening of Caribbean IBC regimes (s.5B: companies registered from 1
January 2019 are liable to pay income tax as a resident). The Financial
Services Regulatory Authority (`fsrastlucia.org`) separately organises
the rest of the international/offshore sector as three licensed
verticals (International Banking Act Cap. 12.17, International
Insurance Act Cap. 12.15, International Mutual Funds Act Cap. 12.16)
plus a Registered Agent and Trustee Licencing Act (Cap. 12.12), not a
single generic IBC vehicle. This ADR documents these findings (see
`src/marketentry/facts.cljc`'s namespace docstring for the full
citations) but deliberately does NOT fold them into the executable
catalog or governor checks: this blueprint's domain
(`:public-sector/market-entry-compliance`) is about bidding into PUBLIC
PROCUREMENT, and forming an IBC is a different market-entry act
(offshore company formation) that does not fit that shape -- a smaller,
honest, in-scope catalog is preferred over stretching the flagship
check to cover an out-of-domain mechanism.

### `statute.facts` (second, orthogonal catalog)

Three Saint Lucia statutes, all read directly from the AG Chambers'
"Revised Laws of Saint Lucia (2023)" web edition (cross-validated
against a second, independently-hosted `pdftotext`-read PDF for the
Labour Act): the Companies Act (s.8, as above), the Labour Act (Acts 37
of 2006, 6 of 2011 and 5 of 2020 -- cited internally in its own s.1(1)
as "the Labour Code 2006"; Division 10 "Termination of Employment"
ss.128-159, the same consolidated-code shape ATG's Labour Code and
GRD's Employment Act use), and the Data Protection Act (Act 11 of 2011
-- honestly reported as only PARTIALLY in force: Part I, ss.32-43 of
Part III, Part VI and Schedule 2(a)-(g) commenced 31 January 2023 per
S.I. 4/2023, roughly twelve years after enactment).

## Consequences

- `src/` now genuinely exists with real, tested, curl/WebFetch/
  `pdftotext`-cited content for this blueprint's declared domain
  (`:public-sector/market-entry-compliance`) -- moves this repo's
  `manifest/itonami-fleet-audit.edn` `:prod-ready?` signal from `:stub`
  to `:active`.
- `CONTRIBUTING.md`/`GOVERNANCE.md`'s leftover Cambodia (`iso3166-khm`)
  template text is corrected to Saint Lucia throughout.
- The existing `culture.facts` catalog (Wave 1, unrelated batch) is
  untouched.
- The Act's own general debarment/suspension power (s.114) and its
  own de-minimis/major-value thresholds (Schedule 1) are genuine,
  verified, NOT-fully-implemented extension points for a future
  iteration (e.g. a second check on the "major value" Board-approval
  threshold itself).
- The International Business Companies Act (Act 40 of 1999) and the
  FSRA's international-sector Acts are genuine, verified, deliberately
  NOT-catalogued extension points -- a future iteration targeting an
  offshore-company-formation market-entry blueprint (a different
  domain than this one) could build on the citations already gathered
  here rather than re-researching them.
- `irdstlucia.gov.lc` currently resolving to a generic placeholder, and
  the "TIN" terminology gap, are honest, disclosed research limits --
  this ADR cites the confirmed "Company Tax Registration" mechanism in
  their place, rather than inventing or assuming a URL or an acronym.
- Sibling country blueprints can continue forking JPN/ATG/DMA/GRD/LCA
  and swapping in their own genuinely-researched `marketentry.facts` /
  `statute.facts` content and whichever flagship check their own law
  actually supports -- this ADR is itself further evidence that even a
  variable-bounded-duration check family has room for a genuinely new
  member when its own statute's shape differs from every fixed-constant
  precedent.
