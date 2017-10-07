(ns name-bazaar.ui.pages.offering-requests-search-page
  (:require
    [cljs-react-material-ui.reagent :as mui]
    [clojure.string :as string]
    [district0x.ui.components.input :refer [input]]
    [district0x.ui.components.misc :as d0x-misc :refer [row row-with-cols col paper page]]
    [district0x.ui.components.text-field :refer [text-field]]
    [district0x.ui.utils :as d0x-ui-utils :refer [format-eth-with-code]]
    [medley.core :as medley]
    [name-bazaar.ui.components.app-layout :refer [app-layout]]
    [name-bazaar.ui.components.icons :as icons]
    [name-bazaar.ui.components.keyword-position-select :refer [keyword-position-select]]
    [name-bazaar.ui.components.misc :refer [a]]
    [name-bazaar.ui.components.offering-request.list-item :refer [offering-request-list-item]]
    [name-bazaar.ui.components.search-results.infinite-list :refer [search-results-infinite-list]]
    [name-bazaar.ui.constants :as constants]
    [name-bazaar.ui.styles :as styles]
    [name-bazaar.ui.utils :refer [etherscan-ens-url]]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]
    [soda-ash.core :as ui]))

(defn offerings-keyword-text-field []
  (let [search-params (subscribe [:offering-requests.main-search/params])]
    (fn []
      [input
       {:label "Keyword"
        :fluid true
        :value (print.foo/look (:name @search-params))
        :on-change #(dispatch [:district0x.location/add-to-query {:name (aget %2 "value")}])}])))

(defn offering-requests-keyword-position-select []
  (let [search-params (subscribe [:offering-requests.main-search/params])]
    (fn []
      [keyword-position-select
       {:value (:name-position @search-params)
        :fluid true
        :on-change #(dispatch [:district0x.location/add-to-query {:name-position (aget %2 "value")}])}])))

(defn search-params-panel []
  [ui/Segment
   [ui/Grid
    {:celled :internally}
    [ui/GridRow
     [ui/GridColumn
      {:computer 10
       :tablet 10
       :mobile 16}
      [offerings-keyword-text-field]]
     [ui/GridColumn
      {:class "hide-divider mobile-join-upper"
       :computer 6
       :tablet 6
       :mobile 16}
      [offering-requests-keyword-position-select]]]
    ]])

(defn offering-requests-search-results []
  (let [search-results (subscribe [:offering-requests/main-search])]
    (fn []
      (let [{:keys [:items :loading? :params :total-count]} @search-results]
        [ui/Segment
         [search-results-infinite-list
          {:class "primary offering-requests-search-results"
           :total-count total-count
           :offset (:offset params)
           :loading? loading?
           :no-items-text "No offering requests found matching your search criteria"
           :on-next-load (fn [offset limit]
                           (dispatch [:offering-requests.main-search/set-params-and-search
                                      {:offset offset :limit limit}
                                      {:append? true}]))}
          (doall
            (for [[i offering-request] (medley/indexed items)]
              [offering-request-list-item
               {:key i
                :offering-request offering-request}]))]]))))

(defmethod page :route.offering-requests/search []
  [app-layout
   [search-params-panel]
   [offering-requests-search-results]])
