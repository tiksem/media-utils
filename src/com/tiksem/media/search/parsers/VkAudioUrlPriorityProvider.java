package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import com.utils.framework.CollectionUtils;
import com.utils.framework.strings.Strings;

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
    private boolean titleContains;
    private boolean titleContainsInBrackets = false;
    private boolean titleEquals;
    private boolean artistContains;
    private boolean artistEqualsOrContains;

    private enum Priority {
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_EQUALS,
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD,
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION,
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS,
        ARTIST_EQUALS_TITLE_EQUALS,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_EQUALS,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS,

        ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD,
        ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION,
        ARTIST_EQUALS_TITLE_CONTAINS,
        ARTIST_CONTAINS_TITLE_EQUALS,
        ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD,
        ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION,
        ARTIST_CONTAINS_TITLE_CONTAINS,

        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS,
        ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS,
        ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS,

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
        if (!artistEquals) {
            artistContains = artistContains();
        }
        artistEqualsOrContains = artistEquals || artistContains;

        titleEquals = inputTitle.equals(vkTitle);
        if (!titleEquals) {
            titleContains = titleContains();
            if (titleContains) {
                titleContainsInBrackets = titleContainsInBrackets();
                titleContains = !titleContainsInBrackets;
            }
        }

        if (artistEqualsOrContains) {
            if (titleEquals || titleContains) {
                return getPriorityFromFristTwoGroups();
            } else if(titleContainsInBrackets) {
                if (artistEquals) {
                    if (durationEquals) {
                        return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS.ordinal();
                    } else {
                        return Priority.ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS.ordinal();
                    }
                } else {
                    if (durationEquals) {
                        return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS.ordinal();
                    } else {
                        return Priority.ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS.ordinal();
                    }
                }
            }
        } else if(durationEquals) {
            return Priority.DURATION_EQUALS.ordinal();
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

            if (containsLive(vk) != containsLive(input)) {
                return false;
            }

            if (Strings.getFirstUnsignedInteger(vk) != Strings.getFirstUnsignedInteger(input)) {
                if (vkDuration != inputDuration) {
                    return false;
                }
            }

            if (containsRadioRip(vk) != containsRadioRip(input)) {
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
                || input.contains("минусовка") || input.contains("instumental") || input.contains("guitar only") ||
                input.contains("без слов") || input.contains("задавка");
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

    private boolean containsLive(String input) {
        return input.contains("live") || input.contains("лайф") || input.contains("нашествие");
    }

    private boolean containsRadioRip(String input) {
        return input.contains("radiorip") || input.contains("radio rip") || input.contains("записывал с радио");
    }

    private int getPriorityFromFristTwoGroups() {
        if (titleEquals) {
            if (artistEquals) {
                if (durationEquals) {
                    return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_EQUALS.ordinal();
                } else {
                    return Priority.ARTIST_EQUALS_TITLE_EQUALS.ordinal();
                }
            } else {
                if (durationEquals) {
                    return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_EQUALS.ordinal();
                } else {
                    return Priority.ARTIST_CONTAINS_TITLE_EQUALS.ordinal();
                }
            }
        } else {
            if (titleContains) {
                if (hasOriginal()) {
                    if (artistEquals) {
                        if (durationEquals) {
                            return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD.ordinal();
                        } else {
                            return Priority.ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD.ordinal();
                        }
                    } else {
                        if (durationEquals) {
                            return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD.ordinal();
                        } else {
                            return Priority.ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD.ordinal();
                        }
                    }
                } else {
                    if (hasItunesSession()) {
                        if (artistEquals) {
                            if (durationEquals) {
                                return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION.ordinal();
                            } else {
                                return Priority.ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION.ordinal();
                            }
                        } else {
                            if (durationEquals) {
                                return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION.ordinal();
                            } else {
                                return Priority.ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION.ordinal();
                            }
                        }
                    }
                }

                if (artistEquals) {
                    if (durationEquals) {
                        return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS.ordinal();
                    } else {
                        return Priority.ARTIST_EQUALS_TITLE_CONTAINS.ordinal();
                    }
                } else {
                    if (durationEquals) {
                        return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS.ordinal();
                    } else {
                        return Priority.ARTIST_EQUALS_TITLE_CONTAINS.ordinal();
                    }
                }
            }

            return (durationEquals ? Priority.DURATION_EQUALS : Priority.TRASH).ordinal();
        }
    }

    private boolean titleContains() {
        return contains(vkTitle, inputTitle);
    }

    private boolean titleContainsInBrackets() {
        int indexOfFirstBracket = vkTitle.indexOf('(');
        if (indexOfFirstBracket < 0) {
            return false;
        }

        int indexOfSecondBracket = vkTitle.indexOf(')');
        if (indexOfSecondBracket < 0) {
            return false;
        }

        int indexOfTitle = vkTitle.indexOf(inputTitle);
        if (indexOfTitle < 0) {
            throw new RuntimeException("Unexpected WTF?");
        }

        return indexOfFirstBracket < indexOfTitle && indexOfSecondBracket > indexOfTitle + inputTitle.length();
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
