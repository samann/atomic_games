(ns othello.util)

(defn file-exists? [file-path]
  (.exists (clojure.java.io/as-file file-path)))

(defn contains-keys? [m & ks]
  (every? true? (map #(contains? m %) ks)))

(defn exec-and-wait [millis f & args]
  (let [result (apply f args)]
    (when (pos? millis)
      (Thread/sleep millis))
    result))

(defmacro time-limited [ms & body]
  `(let [f# (future ~@body)]
     (.get f# ~ms java.util.concurrent.TimeUnit/MILLISECONDS)))
