(ns name-bazaar.ui.pages.offering-detail-page
  (:require
    [cljs-react-material-ui.reagent :as mui]
    [cljs-time.coerce :as time-coerce :refer [to-epoch to-date]]
    [cljs-time.core :as t]
    [district0x.ui.components.misc :as misc :refer [row row-with-cols col paper page]]
    [district0x.ui.components.transaction-button :refer [transaction-button]]
    [district0x.ui.utils :refer [to-locale-string time-unit->text pluralize]]
    [name-bazaar.shared.utils :refer [name-level]]
    [name-bazaar.ui.components.app-layout :refer [app-layout]]
    [name-bazaar.ui.components.misc :refer [a]]
    [name-bazaar.ui.components.offering.action-form :refer [action-form]]
    [name-bazaar.ui.components.offering.general-info :refer [offering-general-info]]
    [name-bazaar.ui.components.offering.list-item :refer [offering-list-item]]
    [name-bazaar.ui.components.offering.warnings :refer [non-ascii-characters-warning missing-ownership-warning sub-level-name-warning]]
    [name-bazaar.ui.components.search-results.infinite-list :refer [search-results-infinite-list]]
    [name-bazaar.ui.components.loading-placeholders :refer [content-placeholder]]
    [name-bazaar.ui.constants :as constants]
    [name-bazaar.ui.styles :as styles]
    [name-bazaar.ui.utils :refer [namehash sha3 strip-eth-suffix offering-type->text]]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]
    [medley.core :as medley]
    [soda-ash.core :as ui]))

(def offering-status->text
  {:offering.status/emergency "Emergency Cancel"
   :offering.status/active "Active"
   :offering.status/finalized "Completed"
   :offering.status/missing-ownership "Missing Ownership"
   :offering.status/auction-ended "Auction Ended"})

(defn offering-status-chip []
  (let [route-params (subscribe [:district0x/route-params])]
    (fn [props]
      (let [status @(subscribe [:offering/status (:offering/address @route-params)])]
        [:div.offering-chip.offering-status
         {:class (name status)}
         (offering-status->text status)]))))

(defn offering-type-chip []
  (let [offering (subscribe [:offerings/route-offering])]
    (fn [props]
      (let [{:keys [:offering/auction? :offering/type]} @offering]
        [:div.offering-chip.offering-type
         {:class type}
         (offering-type->text type)]))))

(defn auction-offering-countdown []
  (let [offering (subscribe [:offerings/route-offering])]
    (fn [props]
      (let [{:keys [:offering/address]} @offering
            time-remaining @(subscribe [:auction-offering/end-time-countdown address])]
        [ui/Grid
         {:columns "equal"
          :divided true
          :text-align :center
          :vertical-align :middle
          :style {:height "50px"}}
         (for [unit [:days :hours :minutes :seconds]]
           (let [amount (get time-remaining unit 0)]
             [ui/GridColumn
              {:key unit}
              [:div.stat-number amount]
              [:div.time-unit (pluralize (time-unit->text unit) amount)]]))]))))

(defn warnings [{:keys [:offering] :as props}]
  (let [{:keys [:offering/address :offering/contains-non-ascii? :offering/top-level-name? :offering/original-owner]} offering]
    [:div
     (r/merge-props
       {:style styles/full-width}
       (dissoc props :offering))
     (when @(subscribe [:offering/missing-ownership? address])
       [missing-ownership-warning
        {:offering/original-owner original-owner}])
     (when (not top-level-name?)
       [sub-level-name-warning
        {:offering/name name
         :style styles/margin-top-gutter-mini}])
     (when contains-non-ascii?
       [non-ascii-characters-warning
        {:style styles/margin-top-gutter-mini}])]))

(defn offering-stats [{:keys [:offering]}]
  (let [offering (subscribe [:offerings/route-offering])]
    (fn []
      (let [{:keys [:offering/price :offering/type :offering/auction? :auction-offering/bid-count]} @offering
            price-formatted (str (to-locale-string price 4) " ETH")]
        [:div.offering-stats
         {:class type}
         (if auction?
           [ui/Grid
            {:celled true
             :columns 3
             :centered true}
            [ui/GridColumn
             {:width 8
              :class :price}
             [:div.offering-stat
              [:h5.ui.header.sub
               (if (pos? bid-count) "Highest Bid" "Starting Price")]
              [:div.stat-number price-formatted]]]
            [ui/GridColumn
             {:width 8
              :class :bid-count}
             [:div.offering-stat
              [:h5.ui.header.sub "Number of bids"]
              [:div.stat-number bid-count]]]
            [ui/GridRow
             [ui/GridColumn
              {:width 16
               :class :time-remaining}
              [:div.offering-stat
               [:h5.ui.header.sub "Time Remaining"]
               [auction-offering-countdown]]]]]
           [ui/Grid
            {:columns 1
             :celled true}
            [ui/GridColumn
             {:class :price}
             [:div.offering-stat
              [:h5.ui.header.sub "Price"]
              [:div.stat-number price-formatted]]]])]))))

(defn offering-detail []
  (let [offering (subscribe [:offerings/route-offering])]
    (fn []
      [ui/Grid
       {:class "layout-grid submit-footer offering-detail"
        :celled "internally"}
       [ui/GridRow
        [ui/GridColumn
         {:mobile 16
          :computer 8}
         [:div.chips
          [offering-status-chip]
          [offering-type-chip]]
         [:div
          [offering-general-info
           {:offering @offering}]]]
        [ui/GridColumn
         {:mobile 16
          :computer 8}
         [offering-stats]]]
       [ui/GridRow
        ]
       [ui/GridRow
        ]])))

(defn similar-offerings []
  (let [search-results (subscribe [:offerings/similar-offerings])]
    (fn []
      (let [{:keys [:items :loading? :params :total-count]} @search-results]
        [paper
         {:style styles/search-results-paper-secondary}
         [:h1.ui.header
          {:style styles/search-results-paper-headline}
          "Similar Offerings"]
         [search-results-infinite-list
          {:total-count total-count
           :offset (:offset params)
           :loading? loading?
           :no-items-text "No similar offerings found"
           :on-next-load (fn [offset limit]
                           (dispatch [:offerings.similar-offerings/set-params-and-search
                                      {:offset offset :limit limit}
                                      {:append? true}]))}
          (doall
            (for [[i offering] (medley/indexed items)]
              [offering-list-item
               {:key i
                :offering offering}]))]]))))

(defmethod page :route.offerings/detail []
  (let [route-params (subscribe [:district0x/route-params])]
    (fn []
      (let [{:keys [:offering/address]} @route-params
            offering-loaded? @(subscribe [:offering/loaded? address])
            offering @(subscribe [:offering address])]
        [app-layout
         [ui/Segment
          [:h1.ui.header.padded "Offering " (:offering/name offering)]
          (if offering-loaded?
            [offering-detail]
            [:div.padded [content-placeholder]])]
         #_[similar-offerings]]))))
