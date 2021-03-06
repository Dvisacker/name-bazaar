(ns name-bazaar.ui.pages.offerings-search-page
  (:require
    [cljs-react-material-ui.reagent :as ui]
    [clojure.string :as string]
    [district0x.shared.utils :as d0x-shared-utils :refer [non-neg-ether-value?]]
    [district0x.ui.components.misc :as d0x-misc :refer [row row-with-cols col paper page]]
    [district0x.ui.components.text-field :refer [text-field ether-field integer-field ether-field-with-currency]]
    [district0x.ui.utils :as d0x-ui-utils :refer [format-eth-with-code]]
    [medley.core :as medley]
    [name-bazaar.ui.components.icons :as icons]
    [name-bazaar.ui.components.misc :refer [a side-nav-menu-center-layout]]
    [name-bazaar.ui.components.offering.list-item :refer [offering-list-item]]
    [name-bazaar.ui.components.search-fields.keyword-position-select-field :refer [keyword-position-select-field]]
    [name-bazaar.ui.components.search-fields.keyword-text-field :refer [keyword-text-field]]
    [name-bazaar.ui.components.search-fields.offerings-order-by-select-field :refer [offerings-order-by-select-field]]
    [name-bazaar.ui.components.search-results.infinite-list :refer [search-results-infinite-list]]
    [name-bazaar.ui.constants :as constants]
    [name-bazaar.ui.styles :as styles]
    [name-bazaar.ui.utils :refer [etherscan-ens-url]]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]))

(defn offerings-keyword-text-field []
  (let [search-params (subscribe [:offerings.main-search/params])]
    (fn []
      [keyword-text-field
       {:value (:name @search-params)
        :on-change #(dispatch [:district0x.location/add-to-query {:name %2}])}])))

(defn offerings-keyword-position-select-field []
  (let [search-params (subscribe [:offerings.main-search/params])]
    (fn []
      [keyword-position-select-field
       {:value (:name-position @search-params)
        :on-change #(dispatch [:district0x.location/add-to-query {:name-position %3}])}])))

(defn saved-searches-select-field []
  (let [saved-searches (subscribe [:offerings/saved-searches])
        query-string (subscribe [:district0x/query-string])]
    (fn [props]
      [ui/select-field
       (r/merge-props
         (merge
           {:style styles/saved-searches-select-field
            :floating-label-text "Saved Searches"
            :disabled (empty? @saved-searches)
            :on-change #(dispatch [:district0x.location/set-query %3])}
           (when (get @saved-searches @query-string)
             {:value @query-string}))
         props)
       (for [[query text] @saved-searches]
         [ui/menu-item
          {:key query
           :value query
           :primary-text text}])])))

(defn save-search-dialog []
  (let [saved-search-name (r/atom "")]
    (fn [{:keys [:on-confirm :on-cancel] :as props}]
      [ui/dialog
       (r/merge-props
         {:title "Save Search"
          :actions [(r/as-element
                      [ui/flat-button
                       {:label "Cancel"
                        :secondary true
                        :on-click on-cancel}])
                    (r/as-element
                      [ui/flat-button
                       {:label "Save"
                        :primary true
                        :disabled (empty? @saved-search-name)
                        :keyboard-focused true
                        :on-click (fn []
                                    (on-confirm @saved-search-name))}])]}
         (dissoc props :on-confirm :on-cancel))
       [:div
        [row
         [text-field
          {:floating-label-text "Search Name"
           :value @saved-search-name
           :on-change #(reset! saved-search-name %2)}]]
        [row
         {:style styles/margin-top-gutter-less}
         [:small
          "Note: Search is saved only in your browser. It's not stored on a blockchain or a server"]]]])))

(defn save-search-button []
  (let [dialog-open? (r/atom false)
        query-string (subscribe [:district0x/query-string])
        saved-search-active? (subscribe [:offerings.saved-search/active?])]
    (fn [props]
      [:div
       [save-search-dialog
        {:open @dialog-open?
         :on-cancel #(reset! dialog-open? false)
         :on-confirm (fn [saved-search-name]
                       (dispatch [:offerings.saved-searches/add @query-string saved-search-name])
                       (reset! dialog-open? false))}]
       (if @saved-search-active?
         [ui/icon-button
          (r/merge-props
            {:tooltip "Delete this saved search"
             :on-click #(dispatch [:offerings.saved-searches/remove @query-string])}
            props)
          (icons/bookmark-remove)]
         [ui/icon-button
          (r/merge-props
            {:tooltip "Save current search"
             :disabled (empty? @query-string)
             :on-click #(reset! dialog-open? true)}
            props)
          (icons/bookmark-outline)])])))

(defn offering-type-checkbox-group []
  (let [search-params (subscribe [:offerings.main-search/params])]
    (fn [{:keys [:buy-now-checkbox-props :auction-checkbox-props]}]
      [:div
       {:style styles/full-width}
       [ui/checkbox
        (r/merge-props
          {:label "Buy Now Offerings"
           :checked (boolean (:buy-now? @search-params))
           :on-check #(dispatch [:district0x.location/add-to-query {:buy-now? %2}])}
          buy-now-checkbox-props)]
       [ui/checkbox
        (r/merge-props
          {:label "Auction Offerings"
           :checked (boolean (:auction? @search-params))
           :on-check #(dispatch [:district0x.location/add-to-query {:auction? %2}])}
          auction-checkbox-props)]])))

(defn name-level-checkbox-group []
  (let [search-params (subscribe [:offerings.main-search/params])]
    (fn [{:keys [:top-level-checkbox-props :subname-checkbox-props]}]
      [:div
       {:style styles/full-width}
       [ui/checkbox
        (r/merge-props
          {:label "Top Level Names"
           :checked (boolean (:top-level-names? @search-params))
           :on-check #(dispatch [:district0x.location/add-to-query {:top-level-names? %2}])}
          top-level-checkbox-props)]
       [ui/checkbox
        (r/merge-props
          {:label "Subnames"
           :checked (boolean (:sub-level-names? @search-params))
           :on-check #(dispatch [:district0x.location/add-to-query {:sub-level-names? %2}])}
          subname-checkbox-props)]])))

(defn exclude-chars-checkbox-group []
  (let [search-params (subscribe [:offerings.main-search/params])]
    (fn [{:keys [:exclude-numbers-checkbox-props :exclude-spec-chars-checkbox-props]}]
      [:div
       {:style styles/full-width}
       [ui/checkbox
        (r/merge-props
          {:label "Exclude Numbers"
           :checked (boolean (:exclude-numbers? @search-params))
           :on-check #(dispatch [:district0x.location/add-to-query {:exclude-numbers? %2}])}
          exclude-numbers-checkbox-props)]
       [ui/checkbox
        (r/merge-props
          {:label "Exclude Special Char."
           :checked (boolean (:exclude-special-chars? @search-params))
           :on-check #(dispatch [:district0x.location/add-to-query {:exclude-special-chars? %2}])}
          exclude-spec-chars-checkbox-props)]])))

(defn price-text-fields []
  (let [search-params (subscribe [:offerings.main-search/params])]
    (fn [{:keys [:min-price-text-field-props :max-price-text-field-props]}]
      (let [{:keys [:min-price :max-price]} @search-params]
        [row-with-cols
         [col
          {:xs 6}
          [text-field
           (r/merge-props
             {:floating-label-text "Min. Price"
              :full-width true
              :value min-price
              :error-text (when-not (non-neg-ether-value? min-price {:allow-empty? true}) " ")
              :on-change #(dispatch [:district0x.location/add-to-query {:min-price %2}])}
             min-price-text-field-props)]]
         [col
          {:xs 6}
          [text-field
           (r/merge-props
             {:floating-label-text "Max. Price"
              :full-width true
              :value max-price
              :error-text (when-not (non-neg-ether-value? max-price {:allow-empty? true}) " ")
              :on-change #(dispatch [:district0x.location/add-to-query {:max-price %2}])}
             max-price-text-field-props)]]]))))

(defn length-text-fields []
  (let [search-params (subscribe [:offerings.main-search/params])]
    (fn [{:keys [:min-length-text-field-props :max-length-text-field-props]}]
      [row-with-cols
       [col
        {:xs 6}
        [integer-field
         (r/merge-props
           {:floating-label-text "Min. Length"
            :full-width true
            :allow-empty? true
            :value (:min-length @search-params)
            :on-change #(dispatch [:district0x.location/add-to-query {:min-length %2}])}
           min-length-text-field-props)]]
       [col
        {:xs 6}
        [integer-field
         (r/merge-props
           {:floating-label-text "Max. Length"
            :full-width true
            :allow-empty? true
            :value (:max-length @search-params)
            :on-change #(dispatch [:district0x.location/add-to-query {:max-length %2}])}
           max-length-text-field-props)]]])))

(defn order-by-select-field []
  (let [search-params (subscribe [:offerings.main-search/params])]
    (fn [props]
      (let [searching-by-name? (not (empty? (:name @search-params)))]
        [offerings-order-by-select-field
         (r/merge-props
           {:order-by-column (first (:order-by-columns @search-params))
            :order-by-dir (first (:order-by-dirs @search-params))
            :options [:offering.order-by/newest
                      :offering.order-by/most-active
                      :offering.order-by/most-expensive
                      :offering.order-by/cheapest
                      :offering.order-by/ending-soon
                      :offering.order-by/most-relevant]
            :on-change (fn [order-by-column order-by-dir]
                         (dispatch [:district0x.location/add-to-query
                                    {:order-by-columns [(name order-by-column)]
                                     :order-by-dirs [(name order-by-dir)]}]))}
           props)]))))

(defn reset-search-icon-button []
  [ui/icon-button
   {:tooltip "Reset Search"
    :on-click #(dispatch [:district0x.location/set-query ""])}
   (icons/filter-remove)])

(defn search-params-panel []
  [paper
   [row-with-cols
    {:bottom "xs"
     :between "xs"}
    [col
     {:md 5}
     [offerings-keyword-text-field]]
    [col
     {:md 3}
     [row
      {:bottom "xs"}
      [offerings-keyword-position-select-field]]]
    [col
     {:md 4}
     [row
      {:bottom "xs"}
      [saved-searches-select-field]
      [save-search-button]]]]
   [row-with-cols
    {:style styles/margin-top-gutter-less}
    [col
     {:md 4}
     [offering-type-checkbox-group]]
    [col
     {:md 4}
     [name-level-checkbox-group]]
    [col
     {:md 4}
     [exclude-chars-checkbox-group]]]
   [row-with-cols
    {:bottom "xs"}
    [col
     {:md 4}
     [price-text-fields]]
    [col
     {:md 4}
     [length-text-fields]]
    [col
     {:md 4}
     [row
      {:bottom "xs"}
      [order-by-select-field
       {:style styles/offerings-order-by-select-field}]
      [reset-search-icon-button]]]]])

(defn search-params-drawer-mobile []
  (let [open? (subscribe [:offerings.main-search.drawer/open?])]
    (fn []
      [ui/drawer
       {:open-secondary true
        :open @open?
        :on-request-change #(dispatch [:offerings.search-params-drawer/set %])}
       [:div
        {:style styles/offering-search-params-drawer-mobile}
        [row
         [offerings-keyword-position-select-field]]
        [row
         [price-text-fields]]
        [row
         [length-text-fields]]
        [row
         {:style styles/margin-top-gutter}
         [offering-type-checkbox-group]]
        [row
         {:style styles/margin-top-gutter}
         [name-level-checkbox-group]]
        [row
         {:style styles/margin-top-gutter}
         [exclude-chars-checkbox-group]]
        [row
         {:style styles/margin-top-gutter}
         [ui/raised-button
          {:full-width true
           :primary true
           :label "Close"
           :on-click #(dispatch [:offerings.search-params-drawer/set false])}]]
        [row
         {:style styles/margin-top-gutter-less}
         [ui/flat-button
          {:full-width true
           :label "Reset"
           :on-click #(dispatch [:district0x.location/set-query ""])}]]]])))

(defn search-params-panel-mobile []
  [paper
   [search-params-drawer-mobile]
   [offerings-keyword-text-field]
   [order-by-select-field]
   [row
    {:bottom "xs"}
    [saved-searches-select-field]
    [save-search-button]]])


(defn offerings-search-results []
  (let [search-results (subscribe [:offerings/main-search])]
    (fn []
      (let [{:keys [:items :loading? :params :total-count]} @search-results]
        [paper
         {:style styles/search-results-paper}
         [search-results-infinite-list
          {:total-count total-count
           :offset (:offset params)
           :loading? loading?
           :no-items-text "No offerings found matching your search criteria"
           :on-next-load (fn [offset limit]
                           (dispatch [:offerings.main-search/set-params-and-search
                                      {:offset offset :limit limit}
                                      {:append? true}]))}
          (doall
            (for [[i offering] (medley/indexed items)]
              [offering-list-item
               {:key i
                :offering offering}]))]]))))

(defmethod page :route.offerings/search []
  (let [xs-sm? (subscribe [:district0x/window-xs-sm-width?])]
    (fn []
      [side-nav-menu-center-layout
       (if @xs-sm?
         [search-params-panel-mobile]
         [search-params-panel])
       [offerings-search-results]])))
