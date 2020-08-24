package com.tektalk.finalcode;

public class SavedWord {
    String wordId;
    String wordSaved;
    String languageSaved;
    String apiLanguage;
    String wordSavedwithLanguage;


    public SavedWord(){

    }


    public SavedWord(String wordId, String wordSaved, String languageSaved, String apiLanguage, String wordSavedwithLanguage) {
        this.wordId = wordId;
        this.wordSaved = wordSaved;
        this.languageSaved = languageSaved;
        this.apiLanguage = apiLanguage;
        this.wordSavedwithLanguage = wordSavedwithLanguage;
    }

    public String getWordId() {
        return wordId;
    }

    public String getWordSaved() {
        return wordSaved;
    }

    public String getLanguageSaved() {
        return languageSaved;
    }
    public String getApiLanguage() {
        return apiLanguage;
    }
    public String getWordLanguage() {
        return wordSavedwithLanguage;
    }

}
