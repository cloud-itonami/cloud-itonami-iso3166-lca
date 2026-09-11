(ns culture.facts
  "Country-level regional-culture catalog for Saint Lucia (LCA) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"LCA"
   [{:culture/id "lca.dish.green-figs-and-saltfish"
     :culture/name "Green figs and saltfish"
     :culture/country "LCA"
     :culture/kind :dish
     :culture/summary "National dish of Saint Lucia consisting of green bananas (locally called green figs) and saltfish."
     :culture/url "https://en.wikipedia.org/wiki/Saint_Lucian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lca.dish.bouyon"
     :culture/name "Bouyon"
     :culture/country "LCA"
     :culture/kind :dish
     :culture/summary "Thick red-bean one-pot soup meal made of meat, ground provisions and vegetables, a popular dish in Saint Lucia."
     :culture/url "https://en.wikipedia.org/wiki/Saint_Lucian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lca.dish.callaloo"
     :culture/name "Callaloo"
     :culture/country "LCA"
     :culture/kind :dish
     :culture/summary "Caribbean leafy-green dish shared across the region; in Saint Lucia crab callaloo is popular, especially as part of the country's Creole day celebrations."
     :culture/url "https://en.wikipedia.org/wiki/Callaloo"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lca.dish.accra"
     :culture/name "Accra"
     :culture/country "LCA"
     :culture/kind :dish
     :culture/summary "Fried Saint Lucian snack composed of flour, egg, seasoning and saltfish, usually prepared around Easter."
     :culture/url "https://en.wikipedia.org/wiki/Saint_Lucian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lca.beverage.piton-beer"
     :culture/name "Piton"
     :culture/country "LCA"
     :culture/kind :beverage
     :culture/summary "Pilsner beer brand from Saint Lucia, first brewed in Vieux Fort in 1992 and named after the Gros Piton and Petit Piton mountains."
     :culture/url "https://en.wikipedia.org/wiki/Piton_(beer)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lca.festival.saint-lucia-jazz"
     :culture/name "Saint Lucia Jazz and Arts Festival"
     :culture/country "LCA"
     :culture/kind :festival
     :culture/summary "Annual festival held in Saint Lucia since 1992, bringing together local and international musicians, performing artists and artisans."
     :culture/url "https://en.wikipedia.org/wiki/Saint_Lucia_Jazz_and_Arts_Festival"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lca.heritage.pitons"
     :culture/name "Pitons"
     :culture/country "LCA"
     :culture/kind :heritage
     :culture/summary "Two volcanic mountain peaks (Gros Piton and Petit Piton) near Soufrière; the surrounding Pitons Management Area was inscribed as a UNESCO World Heritage Site in 2004."
     :culture/url "https://en.wikipedia.org/wiki/Pitons_(Saint_Lucia)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-lca culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "LCA"))
                 " LCA entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
