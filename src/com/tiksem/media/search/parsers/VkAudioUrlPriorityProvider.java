package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import com.utils.framework.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class VkAudioUrlPriorityProvider implements CollectionUtils.PrioritiesProvider<UrlQueryData> {
    private Audio audio;
    private String vkArtist;
    private String vkTitle;
    private int vkDuration;
    private String inputArtist;
    private String inputTitle;
    private int inputDuration;
    private boolean artistEquals;
    private boolean durationEquals;

    private enum Priority {
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_EQUALS,
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD,
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION,
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_EQUALS,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS,

        ARTIST_EQUALS_TITLE_EQUALS,
        ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD,
        ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION,
        ARTIST_EQUALS_TITLE_CONTAINS,
        ARTIST_CONTAINS_TITLE_EQUALS,
        ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD,
        ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION,
        ARTIST_CONTAINS_TITLE_CONTAINS,

        DURATION_EQUALS,
        TRASH
    }

    public VkAudioUrlPriorityProvider(Audio audio) {
        this.audio = audio;
    }

    @Override
    public int getPriorityOf(UrlQueryData data, int index) {
        vkArtist = data.getArtistName().toLowerCase();
        vkTitle = data.getName().toLowerCase();
        vkDuration = data.getDuration();

        inputArtist = audio.getArtistName().toLowerCase();
        inputTitle = audio.getName().toLowerCase();
        inputDuration = audio.getDuration();

        durationEquals = vkDuration == inputDuration;

        artistEquals = inputArtist.equals(vkArtist);
        boolean artistEqualsOrContains = artistEquals || artistContains();
        
        if (durationEquals) {
            if (artistEqualsOrContains) {
                return getPriorityDependingOnParams();
            } else {
                return Priority.DURATION_EQUALS.ordinal();
            }
        } else if(artistEqualsOrContains) {
            int priority = getPriorityDependingOnParams();
            if (priority < Priority.DURATION_EQUALS.ordinal()) {
                return priority + Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS.ordinal();
            }

            return priority;
        }

        return Priority.TRASH.ordinal();
    }

    private boolean artistContains() {
        return contains(vkArtist, inputArtist);
    }

    private boolean contains(String vk, String input) {
        if (vk.contains(input)) {
            if (containsCover(vk) != containsCover(input)) {
                return false;
            }

            if (containsRemix(vk) != containsRemix(input)) {
                return false;
            }

            if (containsRussianVersion(vk) != containsRussianVersion(input)) {
                return false;
            }

            if (containsAcoustic(vk) != containsAcoustic(input)) {
                return false;
            }

            return containsMinus(vk) == containsMinus(input);
        }

        return false;
    }

    private boolean containsCover(String input) {
        return input.contains("cover") || input.contains("кавер");
    }

    private boolean containsMinus(String input) {
        return input.contains("instrumental") || input.contains("minus") || input.contains("минус")
                || input.contains("минусовка") || input.contains("instumental");
    }

    private boolean containsRemix(String input) {
        return input.contains("remix");
    }

    private boolean containsRussianVersion(String input) {
        return input.contains("русская версия") || input.contains("на русском");
    }

    private boolean containsAcoustic(String input) {
        return input.contains("acoustic");
    }

    private int getPriorityDependingOnParams() {
        if (inputTitle.equals(vkTitle)) {
            if (artistEquals) {
                return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_EQUALS.ordinal();
            } else {
                return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_EQUALS.ordinal();
            }
        } else {
            if (titleContains()) {
                if (hasOriginal()) {
                    if (artistEquals) {
                        return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD.ordinal();
                    } else {
                        return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD.ordinal();
                    }
                } else {
                    if (hasItunesSession()) {
                        if (artistEquals) {
                            return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION.ordinal();
                        } else {
                            return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION.ordinal();
                        }
                    }
                }

                if (artistEquals) {
                    return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS.ordinal();
                } else {
                    return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS.ordinal();
                }
            }

            return (durationEquals ? Priority.DURATION_EQUALS : Priority.TRASH).ordinal();
        }
    }

    private boolean titleContains() {
        return contains(vkTitle, inputTitle);
    }

    private boolean hasItunesSession() {
        return vkTitle.contains("itunes session");
    }

    private boolean hasOriginal() {
        return vkTitle.contains("оригинал");
    }

    @Override
    public int getPrioritiesCount() {
        return Priority.values().length;
    }
}
