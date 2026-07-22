# cloud-itonami-iso3166-lca

Open ISO 3166 Blueprint for **LCA**: Saint Lucia -- **`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a Compliance Advisor +
independent **Market-Entry Compliance Governor** to navigate public-
procurement registration, local business/tax registration, and
regulatory-compliance rules in Saint Lucia, so the operator can win and
service a government contract without hiring a full in-house
compliance department.

## Official surface (curl/WebFetch-verified 2026-07-22/23 -- `slucode.saintlucia.gov.lc` does NOT exist, `NXDOMAIN` on direct lookup; the real discovery path was `www.govt.lc` -> `attorneygeneralchambers.com/laws-of-saint-lucia/`, a full paginated 'Revised Laws of Saint Lucia (2023)' law browser)

- Procurement: the Director of Public Procurement (Ministry responsible
  for finance) and the Central Public Procurement Board, established by
  the Public Procurement Act, Cap. 15.10 (Acts 19 of 2015 and 13 of
  2020, in force 1 June 2021). Day-to-day tenders are published on the
  Ministry of Finance's own live tenders board
  (`www.finance.gov.lc/tenders/`); the Board's own jurisdiction is
  limited to procurement of 'major value' ($500,000 and above, Schedule
  1). An Extraordinary Gazette dated 29 July 2024 confirms the Board is
  genuinely staffed (members appointed for a 2024-2027 term).
- Business registration: the Registry of Companies and Intellectual
  Property (ROCIP), a department of the Attorney General's Chambers,
  established by the Companies and Intellectual Property Registry Act
  (Acts 12 of 2000 and 10 of 2020) -- issues a Certificate of
  Incorporation under the Companies Act (Acts 19 of 1996 through 9 of
  2023) s.8.
- Tax: the Inland Revenue Department, Ministry of Finance, registers
  non-individual enterprises via a separate "Company Tax Registration"
  form (a SEPARATE, subsequent act to ROCIP business registration). This
  catalog does NOT use the term "TIN" for Saint Lucia -- that specific
  terminology could not be independently confirmed this session (see
  `src/marketentry/facts.cljc`).
- International Business Companies Act (Act 40 of 1999, amended through
  Act 2 of 2022) is real and genuinely distinctive but OUT OF SCOPE for
  this blueprint's public-procurement market-entry domain -- documented
  as a supplementary research finding in `src/marketentry/facts.cljc`,
  not folded into the executable catalog or governor checks.

## Implementation (R0)

| Piece | Location |
|---|---|
| Actor namespaces | `src/marketentry/*` |
| Governor | `:market-entry-compliance-governor` |
| Ops | `:engagement/intake` · `:jurisdiction/assess` · `:filing/draft` · `:filing/submit` |
| Flagship HARD check | `suspension-disqualifying` (Public Procurement Act, Cap. 15.10 s.114: the Director may suspend a tenderer/contractor for a statutorily-bounded 6-60 month period; Public Procurement Regulations 2023 reg.25(2)(b) disqualifies participation for the duration of that suspension -- independently recomputed from the engagement's own declared suspension start/duration/submission dates, see `docs/adr/0001-architecture.md`) |
| Compliance catalog | `src/statute/facts.cljc` -- Companies Act, Labour Act, Data Protection Act (partial commencement) |
| Tests | `clojure -M:dev:test` |
| Demo | `clojure -M:dev:run` |
| Architecture ADR | [`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) |

`:filing/submit` is never in any phase's `:auto` set -- human sign-off
is structural, not a rollout milestone.

## No robotics premise -- digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) -- the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set -- it always requires human sign-off.

## What this is NOT

- **Not the government of Saint Lucia.** This blueprint is an
  independent operator the government contracts with or that bids into
  its procurement -- never the government itself, and never an official
  channel.
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Saint Lucia-licensed
  counsel or a registered agent where the law requires licensed
  representation.

## Capability layer

Required capabilities (`blueprint.edn`):

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Saint Lucia:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
