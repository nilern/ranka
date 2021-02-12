(ns simplexample.core
  (:require [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom" :as rdom]
            [reitit.core :as reitit]
            [reitit.frontend.controllers :as controllers]

            [ranka.core :as ranka]))

(def routes
  [["/foo" {:name ::foos
            :controllers [{::ranka/controller-config
                           {:components
                            {:foo {:start (list println "Hello")
                                   :stop (list println "Goodbye")}}}}]}
    ["/:id" {:name ::foo
             :controllers [{::ranka/controller-config {:components {}}}]}]]
   ["/bar" {:name ::bar
            :controllers [{::ranka/controller-config {:components {}}}]}]])

;; define components using the `defnc` macro
(defnc greeting
  "A component which greets a user."
  [{:keys [name]}]
  ;; use helix.dom to create DOM elements
  (d/div "Hello, " (d/strong name) "!"))

(defnc app [{:keys [router]}]
  (let [[state set-state] (hooks/use-state {:name "Ranka User"})]
    (d/div
      (d/h1 "Welcome!")
      ;; create elements out of components
      ($ greeting {:name (:name state)})
      (d/input {:value (:name state)
                :on-change #(set-state assoc :name (.. % -target -value))})
      (d/p (pr-str (ranka/controllers router))))))

(defn ^:export run []
  (let [router (ranka/clip-controllers (reitit/router routes))
        foo-match (reitit/match-by-path router "/foo/5")]
    (rdom/render ($ app {:router router}) (js/document.getElementById "app"))
    (controllers/apply-controllers [] foo-match)
    (controllers/apply-controllers (:controllers foo-match) (reitit/match-by-path router "/bar"))))
