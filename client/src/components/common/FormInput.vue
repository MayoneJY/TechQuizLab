<template>
  <div class="form-group">
    <label v-if="label">{{ label }}</label>
    <input
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :type="type"
      :placeholder="placeholder"
      :required="required"
      class="form-input"
      :class="{ 'form-input-error': error }"
    />
    <span v-if="error" class="form-error-text">{{ error }}</span>
  </div>
</template>

<script setup>
defineProps({
  modelValue: { type: String, default: '' },
  label: { type: String, default: '' },
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  required: { type: Boolean, default: false },
  error: { type: String, default: '' },
})

defineEmits(['update:modelValue'])
</script>

<style scoped>
.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: var(--text-primary);
  font-size: 0.95rem;
}

.form-input {
  width: 100%;
  padding: 0.875rem 1.25rem;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: 12px;
  font-size: 14px;
  color: var(--text-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: 'DungGeunMo', 'Noto Sans KR', sans-serif;
  box-shadow: var(--shadow-inset);
}

.form-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: var(--shadow-md);
  background: var(--bg-card-hover);
}

.form-input::placeholder {
  color: var(--text-muted);
}

.form-input-error {
  border-color: var(--danger);
}

.form-error-text {
  display: block;
  margin-top: 0.5rem;
  color: var(--danger-light);
  font-size: 0.85rem;
}
</style>

