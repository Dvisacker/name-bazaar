(ns name-bazaar.ui.components.loading-placeholders
  (:require [reagent.core :as r]))

(defn list-item-placeholder [{:keys [:visible?] :as props}]
  [:div.loading-placeholder.list-item-placeholder
   (r/merge-props
     (when-not visible? {:class "hidden"})
     (dissoc props :visible?))
   [:div.placeholder-background-masker.masker-1]
   [:div.placeholder-background-masker.masker-2]
   [:div.placeholder-background-masker.masker-3]
   [:div.placeholder-background-masker.masker-4]
   [:div.placeholder-background-masker.masker-5]
   [:div.placeholder-background-masker.masker-6]])

(defn content-placeholder [props]
  [:div.loading-placeholder.content-placeholder
   props
   [:div.placeholder-background-masker.masker-1]
   [:div.placeholder-background-masker.masker-2]
   [:div.placeholder-background-masker.masker-3]
   [:div.placeholder-background-masker.masker-4]
   [:div.placeholder-background-masker.masker-5]
   [:div.placeholder-background-masker.masker-6]])