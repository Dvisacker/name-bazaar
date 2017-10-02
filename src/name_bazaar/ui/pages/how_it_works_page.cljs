(ns name-bazaar.ui.pages.how-it-works-page
  (:require
    [district0x.ui.components.misc :as misc :refer [row row-with-cols col paper page]]
    [name-bazaar.ui.components.app-layout :refer [app-layout]]
    [name-bazaar.ui.components.misc :refer [a]]
    [name-bazaar.ui.styles :as styles]))

(defmethod page :route/how-it-works []
  [app-layout
   [paper
    [:h1
     {:style styles/page-headline}
     "How it works"]
    [:div "Enter text here"]]])
