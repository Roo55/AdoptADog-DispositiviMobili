package com.example.adoptadog.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.adoptadog.models.Dog;
import com.example.adoptadog.repository.DogDetailRepository;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.Collections;
import java.util.List;

public class DogDetailViewModel extends AndroidViewModel {

    private final DogDetailRepository repository;
    private final Translator translator;

    public DogDetailViewModel(Application application) {
        super(application);
        repository = new DogDetailRepository(application);

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.SPANISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();
        translator = Translation.getClient(options);
        translator.downloadModelIfNeeded();
    }

    public LiveData<String> translateText(String text) {
        MutableLiveData<String> translatedText = new MutableLiveData<>();
        if (text != null && !text.isEmpty()) {
            translator.translate(text).addOnSuccessListener(translatedText::setValue);
        } else {
            translatedText.setValue(text);
        }
        return translatedText;
    }

    public LiveData<List<String>> getRandomTraits(int count) {
        MutableLiveData<List<String>> traits = new MutableLiveData<>();
        List<String> allTraits = repository.getMockTraits();
        Collections.shuffle(allTraits);
        traits.setValue(allTraits.subList(0, Math.min(count, allTraits.size())));
        return traits;
    }

    public LiveData<String> getSterilizedStatus(Dog dog) {
        MutableLiveData<String> sterilizedStatus = new MutableLiveData<>();
        sterilizedStatus.setValue(repository.getSterilizedStatus(dog));
        return sterilizedStatus;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        translator.close();
    }
}