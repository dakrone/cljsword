(ns cljsword.core
  (:require [clojure.java.io :refer [file writer]])
  (:import (org.crosswire.jsword.book.sword SwordBookMetaData
                                            BlockType
                                            ZVerseBackend)
           (java.net URI))
  (:gen-class))

(defn parse
  "Returns a lazy seq of verses"
  [filename modpath]
  (let [sbmd (SwordBookMetaData. (file filename) (URI. modpath))
        zv (ZVerseBackend. sbmd BlockType/BLOCK_BOOK)
        state (.initState zv)]
    (let [verses (seq (.getGlobalKeyList zv))]
      (map (fn [verse-key] (.readRawContent zv state verse-key))
           verses))))

(defn -main [& args]
  (when-not (= 3 (count args))
    (println "Usage: ./thing <path/to/esv.conf> <path/to> <output-file>")
    (System/exit 1))
  (let [filename (first args)
        modpath (second args)
        outpath (nth args 2)]
    (println "Reading file" filename "and writing to" outpath)
    (with-open [out (writer outpath)]
      (doall
       (map (fn [s] (.write out (str s "\n")))
            (parse filename modpath))))
    (println "Done.")))

;; lein run -- /home/hinmanm/Downloads/bible/mods.d/esv.conf /home/hinmanm/Downloads/bible/ /tmp/out.txt
