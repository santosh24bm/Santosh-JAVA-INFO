library(tidytext)
library(qdap)# not sure whther its needed -- need for polarity 

text_sentences <- read.csv("C:/santosh info/Azure/TEXT analysis
            sentiment analysis improvement/results for 4 types/RsentimentPkg_result1.csv")

bing_lexicon <- get_sentiments("bing")

unique(bing_lexicon$sentiment)
# [1] "negative" "positive"

afinn_lexicon <- get_sentiments("afinn")


sort(unique(afinn_lexicon$score))

nrc_lexicon <- get_sentiments("nrc")


unique(nrc_lexicon$sentiment)
#[1] "trust"        "fear"         "negative"     "sadness"      "anger"       
#[6] "surprise"     "positive"     "disgust"      "joy"          "anticipation"

#check the poistive and nagative coount in nec_lexicon
#filter funtion from dplyr package 
 pos_words <- filter(nrc_lexicon , sentiment %in% c("positive"))
 neg_words <- filter(nrc_lexicon , sentiment %in% c("negative"))
 nrc_lexicon_Pos_neg_words <- rbind(pos_words, neg_words)

#----
# need to convert the text_sentences$text into tidy format i.e word per row before applying inner join method
# check with qdaps polarity  
 res1_bing <- polarity(text_sentences$text , text_sentences$text)
 res1_bing$all
 res1_bing$group
 
 res1_afinn <- polarity(text_sentences$text , text_sentences$text)
 
 write.table(as.matrix(as.data.frame(res1_afinn$all)), "qdap_polarity_afinn.csv", row.names=FALSE, sep=",")
 
---------------------
  ## polarity(
   
   text_sentences_new
 text sentiment rownum
 1                                    It use to work just fine.  Positive      1
 2                                           Not user friendly.   Neutral      2
 3  Soon as hubby needs a new phone we will say bye bye to att.   Neutral      3
 4                     Even a 5 year old can make a better app.  Positive      4
 5          This may be the end of our years-long relationship.   Neutral      5
 6                            9Wish i could rate it Zero ??????   Sarcasm      6
 7                                                     not bad   Positive      7
 8                                  Get rid of the CrApple ads.   Neutral      8
 9       t's been going on for a while and I'm sick of calling.  Negative      9
 10                                                Irritating!!  Negative     10
 
 #class - data.frame
 
 qdap_test <- polarity(text_sentences_new$text , grouping.var = text_sentences_new$rownum , polarity.frame = key.pol)
 
 #default polarity frmae is key.pol
 
 qdap_test$all
 
 rownum wc   polarity     pos.words  neg.words                                                    text.var
 1       1  6  0.8164966    work, fine          -                                   It use to work just fine.
 2       2  3 -0.5773503 user friendly          -                                          Not user friendly.
 3       3 14  0.0000000             -          - Soon as hubby needs a new phone we will say bye bye to att.
 4       4  9  0.3333333        better          -                    Even a 5 year old can make a better app.
 5       5 10  0.0000000             -          -         This may be the end of our years-long relationship.
 6       6  6  0.0000000             -          -                           9Wish i could rate it Zero ??????
   7       7  2  0.7071068             -        bad                                                    not bad 
 8       8  6  0.0000000             -          -                                 Get rid of the CrApple ads.
 9       9 12 -0.2886751             -       sick      t's been going on for a while and I'm sick of calling.
 10     10  1 -1.0000000             - irritating                                                Irritating!!
 
  
  # below is the genral code in polarity
   
    (pos_score <- polarity(text))
 all total.sentences total.words ave.polarity sd.polarity stan.mean.polarity
 1 all               1           6        0.408          NA                 NA
 > 
   > # Get counts
   > (pos_counts <- counts(pos_score))
 all wc polarity pos.words neg.words                               text.var
 1 all  6    0.408      good         - DataCamp courses are good for learning
 
# check the pattern present in daaframe
 key.pol[grep("stress", x)]  
 
 key.pol is dataframe , x is coulmun 
 
 # New lexicon
 custom_pol <- sentiment_frame(positive.words, c(negative.words, "stressed", "turn back"))
 
 # Compare new score
 polarity(stressed_out, polarity.frame = custom_pol)
 
 key.pol <- sentiment_frame(c(positive.words , "lol" ,"LOL","gr8") , negative.words)
 
 # adding the bing_lexcon (bing_lexicon <- get_sentiments("bing")
 #from tidytext to key.pol(actual- lexicon huliu) 
 
 bing_lexicon[grep("negative",y)]$x) # wrong 
 key.pol.temp <- sentiment_frame(positive.words , c(negative.words, bing_lexicon[grep("negative",y)]$x))
 filter(key.pol.temp , y<0)
 filter(key.pol , y<0)
 
 #now use the key.pol.temp for polarity for the text sheet 
 
 qdap_test <- polarity(text_sentences$text , grouping.var = text_sentences$text , polarity.frame = key.pol)
 xx <- qdap_test$all[c(1,2,3,4,5)]
 
 # check the dofferent words added by bing_lexicon and vice varsa in negative words of key.pol(key.pol.temp here) dictionary 
 kp <- as.data.frame(negative.words)
 b_lex <- as.data.frame(bing_lexicon[grep("negative",y)]$x)
 
 anti_join(kp , b_lex , by= c("negative.words" = "bing_lexicon[grep(\"negative\", y)]$x"))

 
# --- filter test -- temp 
 oo <- rbind("little known")
 > oo
 [,1]          
 [1,] "little known"
 > class(oo)
 [1] "matrix"
 > oo <- as.data.frame(oo)
 > oo
 V1
 1 little known
 > filter(kp , negative.words %in% oo)
 [1] negative.words
 <0 rows> (or 0-length row.names)
 > filter(kp , negative.words %in% oo$V1)
 negative.words
 1   little known 
 #  ---
 
 oo <- as.data.frame( anti_join(kp , b_lex , by= c("negative.words" = "bing_lexicon[grep(\"negative\", y)]$x")))
 
 filter(kp , negative.words %in% oo$V1) # nothing matched , means negative words doesnt have anything that bing doesnt have 
 
 # genearal code ends   
 
 write.csv()