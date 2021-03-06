(ns district0x.ui.location-fx
  (:require
    [bidi.bidi :as bidi]
    [cemerick.url :as url]
    [cljs.spec.alpha :as s]
    [district0x.ui.utils :as d0x-ui-utils]
    [goog.events :as events]
    [medley.core :as medley]
    [re-frame.core :as re-frame :refer [reg-fx]]))

(defn path-for [routes & args]
  (str "#" (apply bidi/path-for routes args)))

(defn set-location-hash! [s]
  (set! (.-hash js/location) s))

(defn nav-to! [route route-params routes]
  (set-location-hash! (medley/mapply path-for routes route route-params)))

(defn set-location-query! [query-params]
  (set-location-hash!
    (str "#" (d0x-ui-utils/current-location-hash)
         (cond
           (map? query-params) (when-let [query (url/map->query query-params)]
                                 (str "?" query))
           (string? query-params) (when (seq query-params)
                                    (str "?" query-params))))))

(defn add-to-location-query! [query-params]
  (let [current-query (:query (d0x-ui-utils/current-url))
        new-query (merge current-query (->> query-params
                                         (medley/remove-keys nil?)
                                         (medley/map-keys name)))]
    (set-location-query! new-query)))

(reg-fx
  :location/nav-to
  (fn [[route route-params routes]]
    (nav-to! route route-params routes)))

(reg-fx
  :location/add-to-query
  (fn [[query-params]]
    (add-to-location-query! query-params)))

(reg-fx
  :location/set-query
  (fn [[query-params]]
    (set-location-query! query-params)))
