(ns name-bazaar.ui.components.misc
  (:require
    [district0x.ui.components.misc :as misc]
    [name-bazaar.ui.constants :as constants]
    [name-bazaar.ui.utils :refer [offerings-newest-url offerings-most-active-url offerings-ending-soon-url]]
    [reagent.core :as r]
    [soda-ash.core :as ui]))

(defn a [props body]
  [misc/a
   (assoc props :routes constants/routes)
   body])




