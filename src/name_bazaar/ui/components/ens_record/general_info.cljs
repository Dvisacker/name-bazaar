(ns name-bazaar.ui.components.ens-record.general-info
  (:require
    [cljs-react-material-ui.reagent :as ui]
    [district0x.shared.utils :refer [empty-address? zero-address?]]
    [district0x.ui.components.misc :as d0x-misc :refer [row row-with-cols col etherscan-link]]
    [name-bazaar.ui.components.misc :refer [a]]
    [name-bazaar.ui.styles :as styles]
    [name-bazaar.ui.utils :refer [namehash]]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]))

(defn ens-record-general-info [{:keys [:ens.record/name] :as props}]
  (let [{:keys [:ens.record/owner :ens.record/resolver]} @(subscribe [:ens/record (namehash name)])]
    [:div.ens-record-general-info.description
     (dissoc props :ens.record/name)
     [:div [:b "ENS Information"]]
     [:div.ellipsis
      "Owner: " (cond
                  (not (empty-address? owner))
                  [a {:route :route.user/offerings
                      :route-params {:user/address owner}}
                   owner]

                  (zero-address? owner)
                  "none"

                  :else "")]
     [:div.ellipsis
      "Resolver: " (if (zero-address? resolver)
                     "none"
                     [etherscan-link {:address resolver}])]]))
