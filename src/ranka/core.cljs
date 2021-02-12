(ns ranka.core
  (:require [reitit.core :as reitit]
            [juxt.clip.core :as clip]))

(defn controllers [router]
  (->> (reitit/routes router)
       (map (fn [[_ data]] (select-keys data [:name :controllers])))))

(defn- clip-controller [ctrl]
  (let [config (::controller-config ctrl)
        system (volatile! nil)]
    (-> ctrl
        (dissoc ::controller-config)
        (assoc :start (fn [_] (vreset! system (clip/start config)))
               :stop (fn [_] (clip/stop config @system))))))

(defn clip-controllers [router]
  (reitit/router
    (mapv (fn [[path data]] [path (update data :controllers #(mapv clip-controller %))])
          (reitit/routes router))
    (reitit/options router)))
