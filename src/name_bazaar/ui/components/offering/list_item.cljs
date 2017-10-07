(ns name-bazaar.ui.components.offering.list-item
  (:require
    [cljs-react-material-ui.reagent :as mui]
    [cljs-time.core :as t]
    [clojure.string :as string]
    [district0x.shared.utils :refer [empty-address?]]
    [district0x.ui.components.misc :as d0x-misc :refer [row row-with-cols col paper]]
    [district0x.ui.utils :as d0x-ui-utils :refer [format-eth-with-code format-local-datetime time-ago time-remaining-biggest-unit time-unit->text pluralize time-unit->short-text]]
    [name-bazaar.ui.components.add-to-watched-names-button :refer [add-to-watched-names-button]]
    [name-bazaar.ui.components.ens-record.etherscan-link :refer [ens-record-etherscan-link]]
    [name-bazaar.ui.components.infinite-list :refer [expandable-list-item]]
    [name-bazaar.ui.components.loading-placeholders :refer [list-item-placeholder]]
    [name-bazaar.ui.components.misc :refer [a]]
    [name-bazaar.ui.components.offering.action-form :refer [action-form]]
    [name-bazaar.ui.components.offering.bottom-section :refer [offering-bottom-section]]
    [name-bazaar.ui.components.offering.chips :refer [offering-active-chip offering-sold-chip offering-bought-chip offering-auction-winning-chip offering-auction-pending-returns-chip offering-missing-ownership-chip]]
    [name-bazaar.ui.components.offering.general-info :refer [offering-general-info]]
    [name-bazaar.ui.components.offering.middle-section :refer [offering-middle-section]]
    [name-bazaar.ui.components.offering.warnings :refer [non-ascii-characters-warning missing-ownership-warning sub-level-name-warning]]
    [name-bazaar.ui.styles :as styles]
    [name-bazaar.ui.utils :refer [etherscan-ens-url path-for offering-type->text]]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]
    [soda-ash.core :as ui]
    [name-bazaar.ui.constants :as constants]))

(defn offering-detail-link [{:keys [:offering/address]}]
  [:div
   [:a.no-decor
    {:href (path-for :route.offerings/detail {:offering/address address})}
    "Open Offering Detail"]])

(defn links-section [{:keys [:offering]}]
  (let [{:keys [:offering/address :offering/name]} offering]
    [:div.description.links-section
     [offering-detail-link
      {:offering/address address}]
     [ens-record-etherscan-link
      {:ens.record/name name}]
     [add-to-watched-names-button
      {:ens.record/name name}]]))

(defn offering-expanded-body []
  (let [xs? (subscribe [:district0x.screen-size/mobile?])]
    (fn [{:keys [:offering :hide-action-form?]}]
      (let [{:keys [:offering/address :offering/name :offering/contains-non-ascii? :offering/top-level-name?
                    :offering/original-owner]} offering]

        [ui/Grid
         {:class "layout-grid submit-footer offering-detail"
          :celled :internally}
         [ui/GridRow
          [ui/GridColumn
           {:computer 8
            :tablet 8
            :mobile 16}
           [offering-general-info
            {:offering offering}]]
          [ui/GridColumn
           {:computer 8
            :tablet 8
            :text-align :right
            :mobile 16}
           [links-section
            {:offering offering}]]]
         [ui/GridRow
          [offering-middle-section
           {:offering offering}]]
         [ui/GridRow
          {:centered true}
          [offering-bottom-section
           {:offering offering}]]]))))

(defn auction-bid-count [{:keys [:auction-offering/bid-count]}]
  (when bid-count
    [:span
     {:style styles/offering-list-item-time-left}
     bid-count " " (d0x-ui-utils/pluralize "bid" bid-count)]))

(defn offering-header-price [{:keys [:offering]}]
  [:span.offering-price
   (format-eth-with-code (:offering/price offering))])

(defn offering-header-active-chip [{:keys [:offering] :as props}]
  (when @(subscribe [:offering/active? (:offering/address offering)])
    [offering-active-chip (dissoc props :offering)]))

(defn offering-header-auction-winning-chip [{:keys [:offering] :as props}]
  (when @(subscribe [:auction-offering/active-address-winning-bidder? (:offering/address offering)])
    [offering-auction-winning-chip
     (r/merge-props
       {:won? (t/after? (t/now) (:auction-offering/end-time offering))}
       (dissoc props :offering))]))

(defn offering-header-auction-pending-returns-chip [{:keys [:offering] :as props}]
  (when (pos? @(subscribe [:auction-offering/active-address-pending-returns (:offering/address offering)]))
    [offering-auction-pending-returns-chip
     (dissoc props :offering)]))

(defn offering-header-missing-ownership-chip [{:keys [:offering] :as props}]
  (when @(subscribe [:offering/missing-ownership? (:offering/address offering)])
    [offering-missing-ownership-chip
     (dissoc props :offering)]))

(defn offering-header-offering-type [{:keys [:offering]}]
  (let [offering-type (:offering/type offering)]
    [:h6.ui.sub.header.offering-type
     {:class offering-type}
     (offering-type->text offering-type)]))

(defn offering-header-main-text [{:keys [:offering :show-created-on?]}]
  (let [{:keys [:offering/created-on :offering/name]} offering]
    [:div.offering-main-text
     (if show-created-on?
       (format-local-datetime created-on)
       name)]))

(defn offering-list-item-header-mobile []
  (let [visible? (r/atom false)]
    (fn [{:keys [:offering :show-created-on? :show-sold-for? :show-bought-for?
                 :show-finalized-on? :show-active? :show-auction-winning?
                 :show-auction-pending-returns? :show-missing-ownership?]}]
      (let [{:keys [:offering/address :offering/auction? :offering/price
                    :offering/type :offering/finalized-on :offering/new-owner :auction-offering/bid-count
                    :auction-offering/end-time]} offering]
        [:div.search-results-list-item.mobile
         (if-not address
           [list-item-placeholder
            {:class "short"}]
           [:div.search-results-list-item-header
            {:class (when @visible? :opacity-1)
             :ref #(reset! visible? true)}                  ;; For fade-in animation
            [:div.left-section
             [offering-header-offering-type
              {:offering offering}]
             [offering-header-main-text
              {:show-created-on? show-created-on?
               :offering offering}]
             [offering-header-price
              {:offering offering}]]
            [:div.right-section
             {:class (when (and address
                                (or (not auction?)
                                    (and auction? end-time bid-count)))
                       :opacity-1)}
             (when auction?
               [:div.offering-auction-item
                [:div.amount bid-count]
                [:div.text "Bids"]])
             (when auction?
               (let [[unit amount] (time-remaining-biggest-unit (t/now) end-time)]
                 [:div.offering-auction-item.time-left
                  [:div.amount amount]
                  [:div.text (string/capitalize (str (pluralize (time-unit->text unit) amount) " left"))]]))
             (when-not auction?
               [:div.offering-buy-now-item
                [ui/Button
                 "Buy Now"]])]])

         #_[ui/Transition
            {:class :left-section
             :visible (boolean address)
             :animation "scale"
             :duration 1000}
            [:div.left-section
             [offering-header-offering-type
              {:offering offering}]
             [offering-header-main-text
              {:show-created-on? show-created-on?
               :offering offering}]
             [offering-header-price
              {:offering offering}]]]
         #_(when address
             [:div.right-section
              (when (and auction? bid-count)
                [:div.offering-auction-item
                 [:div.amount bid-count]
                 [:div.text "Bids"]])
              (when (and auction? end-time)
                (let [[unit amount] (time-remaining-biggest-unit (t/now) end-time)]
                  [:div.offering-auction-item.time-left
                   [:div.amount amount]
                   [:div.text (str (pluralize (time-unit->text unit) amount) " left")]]))
              (when-not auction?
                [:div.offering-buy-now-item
                 [ui/Button
                  "Buy Now"]])])


         #_[row-with-cols
            {:style (merge styles/search-results-list-item-header
                           (if address styles/opacity-1 styles/opacity-0))
             :between "sm"
             :middle "sm"}
            [col
             {:xs 12}
             [offering-header-offering-type
              {:offering offering}]
             [offering-header-main-text
              {:show-created-on? show-created-on?
               :offering offering}]]
            [col
             {:xs 4}
             [:div
              {:style (styles/offering-list-item-header-last-col true)}
              [offering-header-price
               {:offering offering}]]]
            [col
             {:xs 8
              :style styles/offering-list-item-bid-count-xs}
             [row
              {:bottom "xs"
               :end "xs"
               :style styles/full-height}
              (when (and auction? (not show-finalized-on?))
                [offering-auction-status-mobile
                 {:offering offering}])

              (when (and show-active?)
                [offering-header-active-chip
                 {:offering offering
                  :style styles/margin-left-gutter-mini}])

              (when (and (or show-sold-for? show-bought-for?)
                         (not show-finalized-on?)
                         (not (empty-address? new-owner)))
                (cond
                  show-sold-for? [offering-sold-chip
                                  {:style styles/margin-left-gutter-mini}]
                  show-bought-for? [offering-bought-chip
                                    {:style styles/margin-left-gutter-mini}]))

              (when show-auction-pending-returns?
                [offering-header-auction-pending-returns-chip
                 {:offering offering
                  :style styles/margin-left-gutter-mini}])

              (when show-auction-winning?
                [offering-header-auction-winning-chip
                 {:offering offering
                  :style styles/margin-left-gutter-mini}])

              (when show-missing-ownership?
                [offering-header-missing-ownership-chip
                 {:offering offering
                  :style styles/margin-left-gutter-mini}])

              (when show-finalized-on?
                (time-ago finalized-on))]]]]))))

(defn offering-list-item-header []
  (let [visible? (r/atom false)]
    (fn [{:keys [:offering :show-created-on? :show-sold-for? :show-bought-for? :show-finalized-on?
                 :show-active? :show-auction-winning? :show-auction-pending-returns?
                 :show-missing-ownership?]}]
      (let [{:keys [:offering/address :offering/auction? :offering/name :offering/price :offering/created-on
                    :offering/type :offering/finalized-on :offering/new-owner :auction-offering/bid-count
                    :auction-offering/end-time]} offering]
        [:div.ui.grid.padded.search-results-list-item
         (if-not address
           [list-item-placeholder]
           [ui/GridRow
            {:ref #(reset! visible? true)                   ;; For fade-in animation
             :class (str "search-results-list-item-header "
                         (when @visible? "opacity-1"))
             :vertical-align :middle}
            [ui/GridColumn
             {:width 4}
             [offering-header-main-text
              {:show-created-on? show-created-on?
               :offering offering}]]
            [ui/GridColumn
             {:width 3
              :text-align :center}
             [offering-header-offering-type
              {:offering offering}]]
            [ui/GridColumn
             {:width 3
              :text-align :center}
             [:div.offering-auction-item
              {:class (if (and auction? bid-count) "opacity-1" "opacity-0")}
              [:div.amount bid-count]]]
            [ui/GridColumn
             {:width 3
              :text-align :center}
             (let [[unit amount] (time-remaining-biggest-unit (t/now) end-time)]
               [:div.offering-auction-item.time-left
                {:class (if (and auction? end-time) :opacity-1 :opacity-0)}
                [:div.amount amount " " [:span.unit (cond-> (time-unit->short-text unit)
                                                      (contains? #{:days :hours} unit) (pluralize amount))]]])]
            [ui/GridColumn
             {:width 3
              :text-align :right}
             [offering-header-price
              {:offering offering}]]]

           #_[row-with-cols
              {:style (merge styles/search-results-list-item-header
                             (if address styles/opacity-1 styles/opacity-0))
               :between "sm"
               :middle "sm"}
              [col
               {:xs 12 :sm 5}
               [offering-header-offering-type
                {:offering offering}]
               [offering-header-main-text
                {:show-created-on? show-created-on?
                 :offering offering}]]
              (when (and (not show-finalized-on?) auction?)
                [col
                 {:sm 2
                  :style styles/text-center}
                 [auction-bid-count
                  {:auction-offering/bid-count bid-count}]])
              (when (and (not show-finalized-on?) auction?)
                [col
                 {:sm 2
                  :style styles/text-center}
                 [auction-time-remaining
                  {:auction-offering/end-time end-time}]])
              (when show-finalized-on?
                [col
                 {:sm 4
                  :style styles/text-center}
                 (time-ago finalized-on)])
              [col
               {:xs 3 :sm 3}
               [:div
                {:style (styles/offering-list-item-header-last-col false)}
                (when show-active?
                  [offering-header-active-chip
                   {:offering offering
                    :style {:margin-right 5}}])
                (when (and (or show-sold-for? show-bought-for?)
                           (not (empty-address? new-owner)))
                  [:span
                   {:style styles/offering-list-item-price-leading-text}
                   (cond
                     show-sold-for? "sold for "
                     show-bought-for? "bought for ")])

                (when show-auction-winning?
                  [offering-header-auction-winning-chip
                   {:offering offering
                    :style {:margin-right 5}}])

                (when show-auction-pending-returns?
                  [offering-header-auction-pending-returns-chip
                   {:offering offering
                    :style {:margin-right 5}}])

                (when show-missing-ownership?
                  [offering-header-missing-ownership-chip
                   {:offering offering
                    :style {:margin-right 5}}])

                [offering-header-price
                 {:offering offering}]]]])]))))

(defn offering-list-item []
  (let [mobile? (subscribe [:district0x.screen-size/mobile?])]
    (fn [{:keys [:offering :expanded? :on-expand :key :header-props :body-props disable-expand? :on-click]
          :as props}]
      (let [{:keys [:offering/address :offering/auction?]} offering
            mobile? (if (:mobile? props) true @mobile?)]
        [expandable-list-item
         {:index key
          :on-collapse #(dispatch [:offerings.list-item/collapsed offering])
          :on-expand #(dispatch [:offerings.list-item/expanded offering])
          :collapsed-height (constants/infinite-list-collapsed-item-height mobile?)
          :disable-expand? (or disable-expand? (not address))
          :on-click on-click}
         (if mobile?
           [offering-list-item-header-mobile
            (merge {:offering offering}
                   header-props)]
           [offering-list-item-header
            (merge {:offering offering}
                   header-props)])
         [offering-expanded-body
          (merge {:offering offering}
                 body-props)]]))))
