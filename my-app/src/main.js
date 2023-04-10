import { createApp } from 'vue'
import App from './App.vue'
import VueKonva from 'vue-konva';
import 'bootstrap/dist/css/bootstrap.css'

const app = createApp(App);
app.use(VueKonva);
app.mount('#app');
