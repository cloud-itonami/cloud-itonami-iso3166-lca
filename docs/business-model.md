# Business Model: Independent Public-Sector Market-Entry & Procurement Compliance Service — Saint Lucia

## Classification

- Repository: `cloud-itonami-iso3166-lca`
- ISO 3166: `LCA` (Saint Lucia)
- Activity: public-procurement market-entry and ongoing regulatory-
  compliance navigation for an already-incorporated operator

## Customer

- an already-incorporated `cloud-itonami-cofog-{code}` /
  `cloud-itonami-isco-{code}` / `cloud-itonami-unspsc-{segment}` /
  `cloud-itonami-{ISIC}` operator wanting to bid on a Saint Lucia public
  contract
- a foreign SME or civic-tech vendor entering the public sector in
  Saint Lucia for the first time
- a `cloud-itonami-M6910` client that has just completed incorporation
  and now needs public-sector market access

## Offer

- registration walkthrough for public procurement of 'major value'
  ($500,000 and above, Schedule 1 of the Public Procurement Act, Cap.
  15.10) requiring Central Public Procurement Board approval, and
  day-to-day tender monitoring via the Ministry of Finance's own
  tenders board (`finance.gov.lc/tenders/`)
- business/tax registration checklist: Certificate of Incorporation
  from the Registry of Companies and Intellectual Property (ROCIP,
  Companies Act s.8), followed by Company Tax Registration with the
  Inland Revenue Department -- a separate, subsequent act
- suspension screening: independent verification that the operator (or
  any of its named directors, shareholders or staff, per s.114(4)) does
  not carry an active suspension under the Public Procurement Act, Cap.
  15.10 s.114 before any filing submission -- including validating that
  a declared suspension's own duration actually falls within s.114(3)'s
  statutory 6-60-month range
- ongoing regulatory-change monitoring subscription
- compliance-audit export package for the client's own records

## Revenue

- per-engagement market-entry fee (one-time registration + checklist
  completion)
- recurring regulatory-change monitoring subscription
- compliance-audit export package

## Trust Controls

- any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off (`:filing/submit` is never automated at any phase)
- a false or fabricated regulatory-requirement claim is a HARD hold that
  cannot be overridden by human approval alone -- it must be corrected
  against a cited official source first
- a suspension under s.114 that is still active as of the engagement's
  own declared submission date (Public Procurement Regulations 2023
  reg.25(2)(b)), OR whose own declared duration falls outside s.114(3)'s
  6-60-month statutory range, is a HARD hold on `:filing/submit` --
  never trusted from a self-reported "clear" claim
- this service does **not** provide legal or tax advice; characterization
  and filing on the client's behalf beyond checklist/draft assistance
  routes to Saint Lucia-licensed counsel or a registered agent

## Boundary with adjacent actors (read before forking)

- **`cloud-itonami-M6910`**: helps a client BECOME a legal entity
  (incorporation, ISIC 6910) -- a prior, different regulatory phase
  (company law). This blueprint assumes incorporation is already done and
  handles public-procurement market entry (a different regulatory domain).
- **`cloud-itonami-cofog-{code}`**: a jurisdiction-agnostic operator
  template for ONE public function. This blueprint is the orthogonal
  jurisdiction-specific axis -- the two compose (fork a COFOG-function
  blueprint AND this one to operate in Saint Lucia).
- **International Business Companies (Act 40 of 1999)**: a DIFFERENT
  market-entry act entirely (offshore company formation, not
  public-procurement bidding) -- deliberately out of scope for this
  blueprint. See `src/marketentry/facts.cljc` for the honest research
  finding on that Act and Saint Lucia's international financial
  services sector (FSRA-regulated international banking/insurance/
  mutual funds).
