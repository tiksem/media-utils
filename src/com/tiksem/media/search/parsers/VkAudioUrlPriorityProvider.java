package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import com.utils.framework.CollectionUtils;
import com.utils.framework.strings.Strings;

public class VkAudioUrlPriorityProvider implements CollectionUtils.PrioritiesProvider<UrlQueryData> {
    private static final float DURATION_DIFF_1_PERCENTAGE = 1.2f;

    private Audio audio;
    private String vkArtist;
    private String vkTitle;
    private int vkDuration;
    private String inputArtist;
    private String inputTitle;
    private int inputDuration;
    private boolean artistEquals;
    private boolean durationEquals;
    private boolean durationDiff1;
    private boolean titleContains;
    private boolean titleContainsInBrackets = false;
    private boolean artistContainsInBrackets = false;
    private boolean titleEquals;
    private boolean artistContains;
    private boolean artistEqualsOrContains;
    private Priority bestPriority = Priority.TRASH;

    public enum Priority {
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_EQUALS,
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD,
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION,
        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS,

        DURATION_DIFF_1_ARTIST_EQUALS_TITLE_EQUALS,

        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_EQUALS,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS,

        DURATION_DIFF_1_ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD,
        DURATION_DIFF_1_ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION,
        DURATION_DIFF_1_ARTIST_EQUALS_TITLE_CONTAINS,
        DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_EQUALS,

        ARTIST_EQUALS_TITLE_EQUALS,

        DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD,
        DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION,
        DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_CONTAINS,

        ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD,
        ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION,
        ARTIST_EQUALS_TITLE_CONTAINS,
        ARTIST_CONTAINS_TITLE_EQUALS,
        ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD,
        ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION,
        ARTIST_CONTAINS_TITLE_CONTAINS,

        DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS,
        DURATION_DIFF_1_ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS,
        ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS,
        DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS,
        DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS,
        ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS,

        DURATION_EQUALS_ARTIST_CONTAINS_IN_BRACKETS_TITTLE_EQUALS,
        DURATION_EQUALS_ARTIST_CONTAINS_IN_BRACKETS_TITTLE_CONTAINS,
        DURATION_EQUALS_ARTIST_CONTAINS_IN_BRACKETS_TITTLE_CONTAINS_IN_BRACKETS,
        ARTIST_CONTAINS_IN_BRACKETS_TITTLE_EQUALS,
        ARTIST_CONTAINS_IN_BRACKETS_TITTLE_CONTAINS,
        ARTIST_CONTAINS_IN_BRACKETS_TITTLE_CONTAINS_IN_BRACKETS,

        DURATION_EQUALS,
        TRASH
    }

    public VkAudioUrlPriorityProvider(Audio audio) {
        this.audio = audio;
    }

    public Priority getBestPriority() {
        return bestPriority;
    }
    
    @Override
    public int getPriorityOf(UrlQueryData data, int index) {
        Priority priority = getPriority(data);
        int ordinal = priority.ordinal();
        if (ordinal < bestPriority.ordinal()) {
            bestPriority = priority;
        }

        return ordinal;
    }

    private Priority getPriority(UrlQueryData data) {
        vkArtist = data.getArtistName().toLowerCase();
        vkTitle = data.getName().toLowerCase();
        vkDuration = data.getDuration();

        inputArtist = audio.getArtistName().toLowerCase();
        inputTitle = audio.getName().toLowerCase();
        inputDuration = audio.getDuration();

        durationEquals = vkDuration == inputDuration;
        if (!durationEquals) {
            if (inputDuration > 0) {
                int max = Math.max(vkDuration, inputDuration);
                int min = Math.min(vkDuration, inputDuration);
                durationDiff1 = (float)max / (float)min <= DURATION_DIFF_1_PERCENTAGE;
            }
        }

        artistEquals = inputArtist.equals(vkArtist);
        if (!artistEquals) {
            artistContains = artistContains();
            if (artistContains) {
                artistContainsInBrackets = artistContainsInBrackets();
                artistContains = !artistContainsInBrackets;
            }
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
                        return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS;
                    } else if(durationDiff1) {
                        return Priority.DURATION_DIFF_1_ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS;
                    } else {
                        return Priority.ARTIST_EQUALS_TITLE_CONTAINS_IN_BRACKETS;
                    }
                } else {
                    if (durationEquals) {
                        return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS;
                    } else if(durationDiff1) {
                        return Priority.DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS;
                    } else {
                        return Priority.ARTIST_CONTAINS_TITLE_CONTAINS_IN_BRACKETS;
                    }
                }
            }
        } else if(artistContainsInBrackets) {
            if (durationEquals) {
                if (titleEquals) {
                    return Priority.DURATION_EQUALS_ARTIST_CONTAINS_IN_BRACKETS_TITTLE_EQUALS;
                } else if(titleContains) {
                    return Priority.DURATION_EQUALS_ARTIST_CONTAINS_IN_BRACKETS_TITTLE_CONTAINS;
                } else if(titleContainsInBrackets) {
                    return Priority.DURATION_EQUALS_ARTIST_CONTAINS_IN_BRACKETS_TITTLE_CONTAINS_IN_BRACKETS;
                }
            } else {
                if (titleEquals) {
                    return Priority.ARTIST_CONTAINS_IN_BRACKETS_TITTLE_EQUALS;
                } else if(titleContains) {
                    return Priority.ARTIST_CONTAINS_IN_BRACKETS_TITTLE_CONTAINS;
                } else if(titleContainsInBrackets) {
                    return Priority.ARTIST_CONTAINS_IN_BRACKETS_TITTLE_CONTAINS_IN_BRACKETS;
                }
            }
        }

        if(durationEquals) {
            return Priority.DURATION_EQUALS;
        }

        return Priority.TRASH;
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

            if (containsBassBooster(input) != containsBassBooster(vk)) {
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
                input.contains("без слов") || input.contains("задавка") || input.contains("инструментал");
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

    private boolean containsBassBooster(String input) {
        return input.contains("bassbooster");
    }

    private Priority getPriorityFromFristTwoGroups() {
        if (titleEquals) {
            if (artistEquals) {
                if (durationEquals) {
                    return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_EQUALS;
                } else if(durationDiff1) {
                    return Priority.DURATION_DIFF_1_ARTIST_EQUALS_TITLE_EQUALS;
                } else {
                    return Priority.ARTIST_EQUALS_TITLE_EQUALS;
                }
            } else {
                if (durationEquals) {
                    return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_EQUALS;
                } else if(durationDiff1) {
                    return Priority.DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_EQUALS;
                } else {
                    return Priority.ARTIST_CONTAINS_TITLE_EQUALS;
                }
            }
        } else {
            if (titleContains) {
                if (hasOriginal()) {
                    if (artistEquals) {
                        if (durationEquals) {
                            return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD;
                        } else if(durationDiff1) {
                            return Priority.DURATION_DIFF_1_ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD;
                        } else {
                            return Priority.ARTIST_EQUALS_TITLE_CONTAINS_ORIGINAL_WORD;
                        }
                    } else {
                        if (durationEquals) {
                            return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD;
                        } else if(durationDiff1) {
                            return Priority.ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD;
                        } else {
                            return Priority.DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_CONTAINS_ORIGINAL_WORD;
                        }
                    }
                } else {
                    if (hasItunesSession()) {
                        if (artistEquals) {
                            if (durationEquals) {
                                return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION;
                            } else if(durationDiff1) {
                                return Priority.DURATION_DIFF_1_ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION;
                            } else {
                                return Priority.ARTIST_EQUALS_TITLE_CONTAINS_ITUNES_SESSION;
                            }
                        } else {
                            if (durationEquals) {
                                return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION;
                            } else if(durationDiff1) {
                                return Priority.DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION;
                            } else {
                                return Priority.ARTIST_CONTAINS_TITLE_CONTAINS_ITUNES_SESSION;
                            }
                        }
                    }
                }

                if (artistEquals) {
                    if (durationEquals) {
                        return Priority.DURATION_EQUALS_ARTIST_EQUALS_TITLE_CONTAINS;
                    } else if(durationDiff1) {
                        return Priority.DURATION_DIFF_1_ARTIST_EQUALS_TITLE_CONTAINS;
                    } else {
                        return Priority.ARTIST_EQUALS_TITLE_CONTAINS;
                    }
                } else {
                    if (durationEquals) {
                        return Priority.DURATION_EQUALS_ARTIST_CONTAINS_TITLE_CONTAINS;
                    } else if(durationDiff1) {
                        return Priority.DURATION_DIFF_1_ARTIST_CONTAINS_TITLE_CONTAINS;
                    } else {
                        return Priority.ARTIST_CONTAINS_TITLE_CONTAINS;
                    }
                }
            }

            return (durationEquals ? Priority.DURATION_EQUALS : Priority.TRASH);
        }
    }

    private boolean titleContains() {
        return contains(vkTitle, inputTitle);
    }

    private boolean containsInBrackets(String input, String vkInput) {
        int indexOfFirstBracket = vkInput.indexOf('(');
        if (indexOfFirstBracket < 0) {
            return false;
        }

        int indexOfSecondBracket = vkInput.indexOf(')');
        if (indexOfSecondBracket < 0) {
            return false;
        }

        int indexOfTitle = vkInput.indexOf(input);
        if (indexOfTitle < 0) {
            throw new RuntimeException("Unexpected WTF?");
        }

        return indexOfFirstBracket < indexOfTitle && indexOfSecondBracket > indexOfTitle + input.length();
    }

    private boolean titleContainsInBrackets() {
        return containsInBrackets(inputTitle, vkTitle);
    }

    private boolean artistContainsInBrackets() {
        return containsInBrackets(inputArtist, vkArtist);
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
