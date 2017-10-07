(ns name-bazaar.ui.components.offering.offerings-order-by-select
  (:require
    [cljs-react-material-ui.reagent :as mui]
    [clojure.set :as set]
    [name-bazaar.ui.styles :as styles]
    [reagent.core :as r]
    [soda-ash.core :as ui]))

(def offerings-order-options
  {:offering.order-by/newest "Newest"
   :offering.order-by/most-active "Most Active"
   :offering.order-by/most-expensive "Most Expensive"
   :offering.order-by/cheapest "Cheapest"
   :offering.order-by/ending-soon "Ending Soon"
   :offering.order-by/finalized-newest "Newest"
   :offering.order-by/finalized-oldest "Oldest"
   :offering.order-by/most-relevant "Most Relevant"})

(def order-option->value
  {:offering.order-by/newest [:created-on :desc]
   :offering.order-by/most-active [:bid-count :desc]
   :offering.order-by/most-expensive [:price :desc]
   :offering.order-by/cheapest [:price :asc]
   :offering.order-by/ending-soon [:end-time :asc]
   :offering.order-by/finalized-newest [:finalized-on :desc]
   :offering.order-by/finalized-oldest [:finalized-on :asc]
   :offering.order-by/most-relevant [:name-relevance :desc]})

(def value->order-option (set/map-invert order-option->value))

(defn offerings-order-by-select [{:keys [:on-change :options :order-by-column :order-by-dir] :as props}]
  [ui/Select
   (r/merge-props
     {:placeholder "Order By"
      :value (value->order-option [(keyword order-by-column) (keyword order-by-dir)])
      :options (for [[value text] (select-keys offerings-order-options options)]
                 {:value value :text text})
      :on-change (fn [e data]
                   (aset data "value" (order-option->value (keyword :offering.order-by (aget data "value"))))
                   (when (fn? on-change)
                     (on-change e data)))}
     (dissoc props :options :order-by-column :order-by-dir :value :on-change))]

  #_[mui/select-field
     (r/merge-props
       {:floating-label-text "Order By"
        :floating-label-style {:left 0}
        :value (str [order-by-column order-by-dir])}
       (merge
         (dissoc props :options :order-by-column :order-by-dir :value-key)
         {:on-change (fn [e index]
                       (let [[order-by-column order-by-dir] (first (nth options-vals index))]
                         (on-change order-by-column order-by-dir)))}))
     (for [[val text] options-vals]
       [mui/menu-item
        {:key (str val)                                     ; hack, because material-ui selectfield
         :value (str val)                                   ; doesn't support non-primitive values
         :primary-text text}])])
