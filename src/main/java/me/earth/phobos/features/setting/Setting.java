package me.earth.phobos.features.setting;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.Feature;

import java.util.function.Predicate;

    public class Setting<T> {
        private final String name;
        private final T defaultValue;
        private T value;
        private T plannedValue;
        private T min;
        private T max;
        private boolean hasRestriction;
        private boolean shouldRenderStringName;
        private Predicate<T> visibility;
        private String description;
        private Feature feature;

        public Setting(String name, T defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.plannedValue = defaultValue;
            this.description = "";
        }

        public Setting(String name, T defaultValue, String description) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.plannedValue = defaultValue;
            this.description = description;
        }

        public Setting(String name, T defaultValue, T min, T max, String description) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.min = min;
            this.max = max;
            this.plannedValue = defaultValue;
            this.description = description;
            this.hasRestriction = true;
        }

        public Setting(String name, T defaultValue, T min, T max) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.min = min;
            this.max = max;
            this.plannedValue = defaultValue;
            this.description = "";
            this.hasRestriction = true;
        }

        public Setting(String name, T defaultValue, T min, T max, Predicate<T> visibility, String description) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.min = min;
            this.max = max;
            this.plannedValue = defaultValue;
            this.visibility = visibility;
            this.description = description;
            this.hasRestriction = true;
        }

        public Setting(String name, T defaultValue, T min, T max, Predicate<T> visibility) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.min = min;
            this.max = max;
            this.plannedValue = defaultValue;
            this.visibility = visibility;
            this.description = "";
            this.hasRestriction = true;
        }

        public Setting(String name, T defaultValue, Predicate<T> visibility) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.visibility = visibility;
            this.plannedValue = defaultValue;
        }

        public String getName() {
            return this.name;
        }

        public T getValue() {
            return this.value;
        }

        public void setValue(T value) {
            this.setPlannedValue(value);
            if (this.hasRestriction) {
                if (((Number) this.min).floatValue() > ((Number) value).floatValue()) {
                    this.setPlannedValue(this.min);
                }
                if (((Number) this.max).floatValue() < ((Number) value).floatValue()) {
                    this.setPlannedValue(this.max);
                }
            }
            ClientEvent event = new ClientEvent(this);
            Phobos.EVENT_BUS.post(event);
            if (!event.isCancelled()) {
                this.value = this.plannedValue;
            } else {
                this.plannedValue = this.value;
            }
        }

        public T getPlannedValue() {
            return this.plannedValue;
        }

        public void setPlannedValue(T value) {
            this.plannedValue = value;
        }

        public T getMin() {
            return this.min;
        }

        public void setMin(T min) {
            this.min = min;
        }

        public T getMax() {
            return this.max;
        }

        public void setMax(T max) {
            this.max = max;
        }

        public void setValueNoEvent(T value) {
            this.setPlannedValue(value);
            if (this.hasRestriction) {
                if (((Number) this.min).floatValue() > ((Number) value).floatValue()) {
                    this.setPlannedValue(this.min);
                }
                if (((Number) this.max).floatValue() < ((Number) value).floatValue()) {
                    this.setPlannedValue(this.max);
                }
            }
            this.value = this.plannedValue;
        }

        public Feature getFeature() {
            return this.feature;
        }

        public void setFeature(Feature feature) {
            this.feature = feature;
        }

        public int getEnum(String input) {
            for (int i = 0; i < this.value.getClass().getEnumConstants().length; ++i) {
                Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
                if (!e.name().equalsIgnoreCase(input)) continue;
                return i;
            }
            return -1;
        }

        public void setEnumValue(String value) {
            for (Enum e : (Enum[]) ((Enum) this.value).getClass().getEnumConstants()) {
                if (!e.name().equalsIgnoreCase(value)) continue;
                this.value = (T) e;
            }
        }

        public String currentEnumName() {
            return EnumConverter.getProperName((Enum) this.value);
        }

        public int currentEnum() {
            return EnumConverter.currentEnum((Enum) this.value);
        }

        public void increaseEnum() {
            this.plannedValue = (T) EnumConverter.increaseEnum((Enum) this.value);
            ClientEvent event = new ClientEvent(this);
            Phobos.EVENT_BUS.post(event);
            if (!event.isCancelled()) {
                this.value = this.plannedValue;
            } else {
                this.plannedValue = this.value;
            }
        }

        public void increaseEnumNoEvent() {
            this.value = (T) EnumConverter.increaseEnum((Enum) this.value);
        }

        public String getType() {
            if (this.isEnumSetting()) {
                return "Enum";
            }
            return this.getClassName(this.defaultValue);
        }

        public <T> String getClassName(T value) {
            return value.getClass().getSimpleName();
        }

        public String getDescription() {
            if (this.description == null) {
                return "";
            }
            return this.description;
        }

        public boolean isNumberSetting() {
            return this.value instanceof Double || this.value instanceof Integer || this.value instanceof Short || this.value instanceof Long || this.value instanceof Float;
        }

        public boolean isEnumSetting() {
            return !this.isNumberSetting() && !(this.value instanceof String) && !(this.value instanceof Bind) && !(this.value instanceof Character) && !(this.value instanceof Boolean);
        }

        public boolean isStringSetting() {
            return this.value instanceof String;
        }

        public T getDefaultValue() {
            return this.defaultValue;
        }

        public String getValueAsString() {
            return this.value.toString();
        }

        public boolean hasRestriction() {
            return this.hasRestriction;
        }

        public void setVisibility(Predicate<T> visibility) {
            this.visibility = visibility;
        }

        public Setting<T> setRenderName(boolean renderName) {
            this.shouldRenderStringName = renderName;
            return this;
        }

        public boolean shouldRenderName() {
            if (!this.isStringSetting()) {
                return true;
            }
            return this.shouldRenderStringName;
        }

        public boolean isVisible() {
            if (this.visibility == null) {
                return true;
            }
            return this.visibility.test(this.getValue());
        }
}
