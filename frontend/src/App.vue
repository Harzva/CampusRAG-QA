<template>
  <div class="container">
    <h1>Campus RAG Demo</h1>
    <section class="upload">
      <h2>Upload Knowledge File</h2>
      <input type="file" @change="onFileChange" />
      <button @click="uploadFile" :disabled="!selectedFile">Upload</button>
      <p v-if="uploadStatus">{{ uploadStatus }}</p>
    </section>
    <section class="chat">
      <h2>Ask a Question</h2>
      <div class="messages" ref="messageContainer">
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="msg.role.toLowerCase()"
          class="message"
        >
          <strong>{{ msg.role }}:</strong> {{ msg.content }}
        </div>
      </div>
      <div class="input-row">
        <input
          v-model="userInput"
          @keyup.enter="sendQuestion"
          placeholder="Type your question..."
        />
        <button @click="sendQuestion" :disabled="!userInput">Send</button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';

const selectedFile = ref(null);
const uploadStatus = ref('');
const userInput = ref('');
const messages = ref([]);
let messageCounter = 0;
const messageContainer = ref(null);

function scrollToBottom() {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
    }
  });
}

function onFileChange(event) {
  selectedFile.value = event.target.files[0];
}

async function uploadFile() {
  if (!selectedFile.value) return;
  const formData = new FormData();
  formData.append('file', selectedFile.value);
  uploadStatus.value = 'Uploading...';
  try {
    const resp = await fetch('/api/documents', {
      method: 'POST',
      body: formData,
    });
    if (resp.ok) {
      uploadStatus.value = 'Upload complete and indexed.';
      selectedFile.value = null;
    } else {
      uploadStatus.value = 'Upload failed: ' + resp.statusText;
    }
  } catch (err) {
    uploadStatus.value = 'Upload error: ' + err.message;
  }
}

async function sendQuestion() {
  const input = userInput.value.trim();
  if (!input) return;
  messages.value.push({ id: ++messageCounter, role: 'User', content: input });
  userInput.value = '';
  scrollToBottom();
  const body = JSON.stringify({ conversationId: 'default', userInput: input });
  try {
    const resp = await fetch('/api/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body,
    });
    if (resp.ok) {
      // Read stream of text events. For demonstration we read as a single chunk.
      const reader = resp.body.getReader();
      const decoder = new TextDecoder();
      let answer = '';
      while (true) {
        const { value, done } = await reader.read();
        if (value) {
          answer += decoder.decode(value, { stream: true });
        }
        if (done) break;
      }
      messages.value.push({ id: ++messageCounter, role: 'Assistant', content: answer });
      scrollToBottom();
    } else {
      messages.value.push({ id: ++messageCounter, role: 'Assistant', content: 'Error: ' + resp.statusText });
    }
  } catch (err) {
    messages.value.push({ id: ++messageCounter, role: 'Assistant', content: 'Error: ' + err.message });
  }
}
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
  font-family: Arial, sans-serif;
}

h1 {
  text-align: center;
  margin-bottom: 24px;
}

.upload,
.chat {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.messages {
  height: 300px;
  border: 1px solid #eee;
  padding: 8px;
  margin-bottom: 8px;
  overflow-y: auto;
  background-color: #fafafa;
}

.message {
  margin-bottom: 4px;
}

.user {
  color: #333;
}

.assistant {
  color: #007bff;
}

.input-row {
  display: flex;
  gap: 8px;
}
.input-row input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.input-row button {
  padding: 8px 16px;
}
</style>