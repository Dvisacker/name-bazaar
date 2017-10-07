(ns name-bazaar.ui.components.keyword-position-select
  (:require
    [reagent.core :as r]
    [soda-ash.core :as ui]))

(defn keyword-position-select [props]
  [ui/Select
   (r/merge-props
     {:options [{:value :any :text "Any"}
                {:value :start :text "Start"}
                {:value :end :text "End"}]}
     props)])
