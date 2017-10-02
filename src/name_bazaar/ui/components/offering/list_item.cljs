(ns name-bazaar.ui.components.offering.list-item
  (:require
    [cljs-react-material-ui.reagent :as mui]
    [cljs-time.core :as t]
    [clojure.string :as string]
    [district0x.shared.utils :refer [empty-address?]]
    [district0x.ui.components.misc :as d0x-misc :refer [row row-with-cols col paper]]
    [district0x.ui.utils :as d0x-ui-utils :refer [format-eth-with-code format-local-datetime time-ago time-remaining-biggest-unit time-unit->text pluralize]]
    [name-bazaar.ui.components.add-to-watched-names-button :refer [add-to-watched-names-button]]
    [name-bazaar.ui.components.ens-record.etherscan-link :refer [ens-record-etherscan-link]]
    [name-bazaar.ui.components.infinite-list :refer [expandable-list-item]]
    [name-bazaar.ui.components.misc :refer [a]]
    [name-bazaar.ui.components.offering.action-form :refer [action-form]]
    [name-bazaar.ui.components.offering.chips :refer [offering-active-chip offering-sold-chip offering-bought-chip offering-auction-winning-chip offering-auction-pending-returns-chip offering-missing-ownership-chip]]
    [name-bazaar.ui.components.offering.general-info :refer [offering-general-info]]
    [name-bazaar.ui.components.offering.warnings :refer [non-ascii-characters-warning missing-ownership-warning sub-level-name-warning]]
    [name-bazaar.ui.components.loading-placeholders :refer [list-item-placeholder]]
    [name-bazaar.ui.styles :as styles]
    [name-bazaar.ui.utils :refer [etherscan-ens-url path-for offering-type->text]]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]
    [soda-ash.core :as ui]))

(defn offering-detail-link [{:keys [:offering/address]}]
  [:div
   [a
    {:route :route.offerings/detail
     :route-params {:offering/address address}
     :style styles/text-decor-none}
    "Open Offering Detail"]])

(defn offering-expanded-body []
  (let [xs? (subscribe [:district0x.screen-size/mobile?])]
    (fn [{:keys [:offering :hide-action-form?]}]
      (let [{:keys [:offering/address :offering/name :offering/contains-non-ascii? :offering/top-level-name?
                    :offering/original-owner]} offering
            show-missing-ownership-warning? @(subscribe [:offering/missing-ownership? address])]
        [row-with-cols
         {:style (styles/search-results-list-item-body @xs?)}
         [col
          {:xs 12 :sm 8 :style styles/margin-bottom-gutter-mini}
          [offering-general-info
           {:offering offering}]]
         [col
          {:xs 12 :sm 4
           :style (styles/list-item-body-links-container @xs?)}
          [offering-detail-link
           {:offering/address address}]
          [ens-record-etherscan-link
           {:ens.record/name name}]
          [add-to-watched-names-button
           {:ens.record/name name}]]
         [col
          {:xs 12}
          (cond
            show-missing-ownership-warning?
            [missing-ownership-warning
             {:offering/original-owner original-owner}]

            (not top-level-name?)
            [sub-level-name-warning
             {:offering/name name}]

            contains-non-ascii?
            [non-ascii-characters-warning])]

         (when-not hide-action-form?
           [col
            {:xs 12
             :style (merge styles/margin-top-gutter-mini
                           styles/text-right
                           styles/full-width)}
            [action-form
             {:offering offering}]])]))))

(defn auction-bid-count [{:keys [:auction-offering/bid-count]}]
  (when bid-count
    [:span
     {:style styles/offering-list-item-time-left}
     bid-count " " (d0x-ui-utils/pluralize "bid" bid-count)]))

(defn auction-time-remaining [{:keys [:auction-offering/end-time]}]
  (when end-time
    [:span
     #_(if (t/after? end-time (t/now))
         (str (format-time-remaining-biggest-unit end-time) " left")
         "finished")]))

(defn offering-header-price []
  (let [xs? (subscribe [:district0x.screen-size/mobile?])]
    (fn [{:keys [:offering]}]
      [:span.offering-price
       (format-eth-with-code (:offering/price offering))])))

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

(defn offering-auction-status-mobile [{:keys [:offering]}]
  [:div
   [auction-bid-count
    {:auction-offering/bid-count (:auction-offering/bid-count offering)}]
   [:span
    {:style {:margin-right 3}}
    ", "]
   [auction-time-remaining
    {:auction-offering/end-time (:auction-offering/end-time offering)}]])

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

(defn offering-list-item-header-mobile [{:keys [:offering :show-created-on? :show-sold-for? :show-bought-for?
                                                :show-finalized-on? :show-active? :show-auction-winning?
                                                :show-auction-pending-returns? :show-missing-ownership?]}]
  (let [{:keys [:offering/address :offering/auction? :offering/price
                :offering/type :offering/finalized-on :offering/new-owner :auction-offering/bid-count
                :auction-offering/end-time]} offering]
    [:div.search-results-list-item
     [list-item-placeholder
      {:class "short"
       :visible? (not address)}]
     [:div.search-results-list-item-header
      {:class (if address :opacity-1 :opacity-0)}
      [:div.left-section
       [offering-header-offering-type
        {:offering offering}]
       [offering-header-main-text
        {:show-created-on? show-created-on?
         :offering offering}]
       [offering-header-price
        {:offering offering}]]
      [:div.right-section
       {:class (if (and address
                        (or (not auction?)
                            (and auction? end-time bid-count)))
                 :opacity-1
                 :opacity-0)}
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
           "Buy Now"]])]]
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
            (time-ago finalized-on))]]]]))

(defn offering-list-item-header [{:keys [:offering :show-created-on? :show-sold-for? :show-bought-for? :show-finalized-on?
                                         :show-active? :show-auction-winning? :show-auction-pending-returns?
                                         :show-missing-ownership?]}]
  (let [{:keys [:offering/address :offering/auction? :offering/name :offering/price :offering/created-on
                :offering/type :offering/finalized-on :offering/new-owner :auction-offering/bid-count
                :auction-offering/end-time]} offering]
    [:div
     {:style (styles/search-results-list-item false)}
     (if-not address
       [list-item-placeholder]
       [row-with-cols
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
           {:offering offering}]]]])]))

(defn offering-list-item []
  (let [xs? (subscribe [:district0x.screen-size/mobile?])]
    (fn [{:keys [:offering :expanded? :on-expand :key :header-props :body-props disable-expand? :on-click]
          :as props}]
      (let [{:keys [:offering/address :offering/auction?]} offering
            xs? (if (:xs? props) true @xs?)]
        [expandable-list-item
         {:index key
          :on-collapse #(dispatch [:offerings.list-item/collapsed offering])
          :on-expand #(dispatch [:offerings.list-item/expanded offering])
          :collapsed-height (styles/search-results-list-item-height xs?)
          :expanded-height (if auction?
                             (styles/auction-offering-list-item-expanded-height xs?)
                             (styles/buy-now-offering-list-item-expanded-height xs?))
          :disable-expand? (or disable-expand? (not address))
          :on-click on-click}
         (if xs?
           [offering-list-item-header-mobile
            (merge {:offering offering}
                   header-props)]
           [offering-list-item-header
            (merge {:offering offering}
                   header-props)])
         [offering-expanded-body
          (merge {:offering offering}
                 body-props)]]))))
