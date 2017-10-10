(ns name-bazaar.ui.components.app-layout
  (:require
    [district0x.ui.components.active-address-balance :refer [active-address-balance]]
    [district0x.ui.components.active-address-select :refer [active-address-select]]
    [district0x.ui.components.snackbar :refer [snackbar]]
    [district0x.ui.components.transaction-log :refer [transaction-log]]
    [name-bazaar.ui.components.app-bar-search :refer [app-bar-search]]
    [name-bazaar.ui.utils :refer [offerings-newest-url offerings-most-active-url offerings-ending-soon-url path-for]]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]
    [soda-ash.core :as ui]))

(defn side-nav-menu-logo []
  [:a.side-nav-logo
   {:href (path-for :route/home)}
   [:img
    {:src "./images/logo@2x.png"}]])

(defn district0x-banner []
  [:div.district0x-banner
   [:div.logo ""]
   [:div "Part of the"]
   [:a
    {:href "https://district0x.io"
     :target :_blank}
    "district0x Network"]])

(def nav-menu-items-props [{:text "Offerings"
                            :route :route.offerings/search
                            :class :offerings-search}
                           {:text "Latest"
                            :href offerings-newest-url
                            :class "nested-item offerings-newest"}
                           {:text "Most Active"
                            :href offerings-most-active-url
                            :class "nested-item offerings-most-active"}
                           {:text "Ending Soon"
                            :href offerings-ending-soon-url
                            :class "nested-item offerings-ending-soon"}
                           {:text "Requests"
                            :route :route.offering-requests/search
                            :class :offering-requests-search}
                           {:text "Watched Names"
                            :route :route/watched-names
                            :class :watched-names}
                           {:text "Create Offering"
                            :route :route.offerings/create
                            :class :create-offering}
                           {:text "My Offerings"
                            :route :route.user/my-offerings
                            :class :my-offerings}
                           {:text "My Purchases"
                            :route :route.user/my-purchases
                            :class :my-purchases}
                           {:text "My Bids"
                            :route :route.user/my-bids
                            :class :my-bids}
                           {:text "My Settings"
                            :route :route.user/my-settings
                            :class :my-settings}
                           {:text "Register Name"
                            :route :route.mock-registrar/register
                            :class :register-name}
                           {:text "How it works"
                            :route :route/how-it-works
                            :class :how-it-works}
                           {:text "About"
                            :route :route/about
                            :class :about}])

(defn app-bar []
  (let [open? (subscribe [:district0x.transaction-log/open?])]
    (fn []
      [:div.app-bar
       [:div.left-section
        [active-address-select]
        [:div.sidebar-toggle
         {:on-click (fn [e]
                      (dispatch [:district0x.menu-drawer/set true])
                      (.stopPropagation e))}]]
       [:div.middle-section
        [app-bar-search]]
       [:div.right-section
        {:on-click #(dispatch [:district0x.transaction-log/set-open (not @open?)])}
        [active-address-balance]
        [:div.transaction-log-icon]]])))

(defn app-layout []
  (let [drawer-open? (subscribe [:district0x/menu-drawer-open?])
        min-computer-screen? (subscribe [:district0x.screen-size/min-computer-screen?])
        active-page (subscribe [:district0x/active-page])
        app-container-ref (r/atom nil)]
    (fn [& children]
      [:div.app-container
       {:ref (fn [el]
               (when (and el (not @app-container-ref))
                 (reset! app-container-ref el)))}
       [ui/Sidebar
        {:as (aget js/semanticUIReact "Menu")
         :visible (or @drawer-open? @min-computer-screen?)
         :animation "overlay"
         :vertical true
         :inverted true
         :fixed :left}
        [:div.menu-content
         {:style {:overflow-y :scroll}}
         [side-nav-menu-logo]
         (doall
           (for [{:keys [:text :route :href :class :on-click]} nav-menu-items-props]
             (let [href (or href (path-for route))]
               [ui/MenuItem
                {:key text
                 :as "a"
                 :href href
                 :class class
                 :on-click #(dispatch [:district0x.window/scroll-to-top])
                 :active (= (str "#" (:path @active-page)) href)}
                text])))
         [district0x-banner]]]
       [:div.app-content
        {:on-click (fn []
                     (when-not @min-computer-screen?
                       (dispatch [:district0x.menu-drawer/set false])))}
        [app-bar]
        [ui/Sticky
         {:class :transaction-log-sticky
          :context @app-container-ref}
         [transaction-log
          {:title-props {:on-click #(dispatch [:district0x.transaction-log/set-open false])}}]]
        [ui/Grid
         {:columns 1
          :centered true
          :padded true}
         (into [ui/GridColumn
                {:class :main-content
                 :widescreen 8
                 :large-screen 12
                 :tablet 14
                 :mobile 15}]
               children)]]
       [snackbar
        {:action-button-props {:primary true
                               :size :small}}]])))