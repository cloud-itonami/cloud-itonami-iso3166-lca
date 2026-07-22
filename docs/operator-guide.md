# Operator Guide

## First Deployment

1. Confirm the client's incorporation/legal-entity status is complete
   (route to `cloud-itonami-M6910` or local counsel first if not).
2. Register the client's intake: business type, target public function,
   prior filing history in Saint Lucia if any.
3. Run the advisor in read-only mode against the Ministry of Finance's
   own live tenders board (`finance.gov.lc/tenders/`), governed by the
   Public Procurement Act, Cap. 15.10 (Acts 19 of 2015 and 13 of 2020,
   in force 1 June 2021) and the Public Procurement Regulations, 2023
   (Statutory Instrument, 2023, No. 133).
4. Compare the checklist against the client's current documentation
   (ROCIP Certificate of Incorporation, Company Tax Registration record
   from the Inland Revenue Department, and confirmation that neither
   the operator nor any named director/shareholder/staff carries an
   active suspension under s.114 -- including that any declared
   suspension's own duration is itself lawful under s.114(3)'s 6-60
   month range).
5. Enable gated filing-draft assistance once the Market-Entry Compliance
   Governor contract is trusted; actual submission always requires human
   sign-off.

## Minimum Production Controls

- client-owned data store for business/tax registration documents
- clear provenance (official portal/regulation citation) for every
  requirement surfaced
- approval workflow for any portal registration or filing submission
- independent re-verification that no active s.114 suspension covers
  the declared submission date, and that any declared suspension
  duration is itself within the 6-60-month statutory range, before any
  `:filing/submit` -- never trust a self-reported "clear" claim
- named referral relationship with Saint Lucia-licensed counsel or a
  registered agent for anything beyond checklist/draft assistance
- monthly audit export
- a suspended tenderer's own remedy, per s.114(5), is to apply to the
  Director (after 6 months from the start of a first suspension) for
  modification of the suspension period -- this actor has no standing
  to make that application on the client's behalf; it only reports the
  suspension record it was given

## Certification

Certified operators must prove data provenance, audit traceability, that
automated actions cannot bypass the Market-Entry Compliance Governor, and
a working referral relationship with Saint Lucia-licensed counsel or a
registered agent for whatever licensed representation the law of
Saint Lucia requires for actual public-procurement filings.
