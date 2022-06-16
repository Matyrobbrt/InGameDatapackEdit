package com.matyrobbrt.igde.util;

import com.matyrobbrt.igde.InGameDatapackEdit;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public enum Translations {
    SEARCH("gui", "search"),
    TAG_EDITOR("gui", "tag_editor"),
    ADDED_OBJECTS("gui", "added_objects"),
    REMOVED_OBJECTS("gui", "removed_objects"),
    UNSAVED_CHANGES("gui", "unsaved_changes"),
    VALUE("gui", "value"),

    NEW_ADDITION("button", "new_addition"),
    NEW_REMOVAL("button", "new_removal"),
    REPLACE_VALUES_DESCRIPTION("button", "replace_values"),
    REPLACE("button", "replace"),
    REPLACE_ON("button", "replace.on"),
    REPLACE_OFF("button", "replace.off"),

    REQUIRED_DESCRIPTION("button", "required.description"),
    REQUIRED("button", "required"),
    REQUIRED_ON("button", "required.on"),
    REQUIRED_OFF("button", "required.off"),

    TAG_HOLDS_VALUES("tooltip", "tag_holds_values"),
    DELETE("tooltip", "delete"),
    OPTIONAL_ENTRY("tooltip", "optional_entry"),
    INVALID_VALUE("tooltip", "invalid_value"),
    DUPLICATE_VALUE("tooltip", "duplicate_value");

    private final String key;
    Translations(String type, String key) {
        this.key = type + "." + InGameDatapackEdit.MOD_ID + "." + key;
    }

    public MutableComponent create(Object... args) {
        return new TranslatableComponent(key, args);
    }
}
