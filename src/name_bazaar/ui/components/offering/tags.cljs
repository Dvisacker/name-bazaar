(ns name-bazaar.ui.components.offering.tags
  (:require
    [name-bazaar.ui.utils :refer [offering-status->text offering-type->text]]
    [re-frame.core :refer [subscribe]]
    [reagent.core :as r]))

(defn offering-status-tag [{:keys [:offering/status]} text]
  [:div.offering-tag.offering-status
   {:class (name status)}
   (or text (offering-status->text status))])

(defn offering-type-tag [{:keys [:offering/type]}]
  [:div.offering-tag.offering-type
   {:class type}
   (offering-type->text type)])

(defn offering-sold-tag [props]
  [:div.offering-tag.sold
   props
   "Sold"])

(defn offering-auction-winning-tag [{:keys [:won?] :as props}]
  [:div.offering-tag.auction-winning
   (dissoc props :won?)
   (if won?
     "Won"
     "Winning")])

(defn offering-auction-pending-returns-tag [props]
  [:div.offering-tag.pending-returns
   props
   "Pending ret."])




