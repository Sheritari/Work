<template>
  <div id="app">

    <div class="d-flex flex-row justify-content-center p-2 text-center" v-if="nameVisibility">
      <div class="align-self-center m-2 txt">Введите название (ключ-значение): </div>
      <input class="form-control w-25 m-2" v-model="message" type="text">
      <input type="submit" v-on:click="clickedOk" class="btn btn-success m-2" style="font-size: 120%;" value="Добавить">
      <input type="submit" v-on:click="getJson" class="btn btn-primary m-2" style="font-size: 120%;" value="Json">
    </div>

    <div class="d-flex flex-row justify-content-center p-2 text-center" v-if="drawKeyVisibility">
      <div class="align-self-center m-2 txt">Выделите значение {{message}} и нажмите кнопку</div>
      <input type="submit" v-on:click="clicks" class="btn btn-secondary m-2" style="font-size: 120%;" value="Далее">
    </div>

    <div class="d-flex flex-row justify-content-center p-2 text-center" style="font-size: 120%;" v-if="drawValueVisibility">
      
      <div class="m-2 txt">Выделите значение: {{message}}</div>
    </div> 

    <div class="d-flex flex-row justify-content-center p-2 text-center" v-if="sendVisibility">
      <div class="m-2 txt">Добавление пары ключ-значение: {{message}}</div>
      <input type="submit" v-on:click="clickSend" class="btn btn-success m-2" style="font-size: 120%;" value="Добавить">
      <input type="submit" v-on:click="clickedCancel" class="btn btn-warning m-2" style="font-size: 120%;" value="Отмена">
    </div>

    <PSPDFKitContainer :pdfFile="pdfFile" @loaded="handleLoaded" />

  </div>
</template>

<script>
import PSPDFKitContainer from "@/components/PSPDFKitContainer";

export default {
  el: '#app',
  data() {
    return {
      pdfFile: this.pdfFile || "/document.pdf",
      nameVisibility: true,
      drawKeyVisibility: false,
      drawValueVisibility: false,
      message: "",
      counter: 0,
      flagDrawSecond: false,
      sendVisibility: false,
      arrayObjects: new Map(),
      firstRectangle: 0,
      secondRectangle: 0,
      inst: "",
      pages: []
    };
  },

  mounted() {

  },

  components: {
    PSPDFKitContainer,
  },

  methods: {
    handleLoaded(instance) {
      console.log("PSPDFKit has loaded: ", instance);
      let countPages = instance.totalPageCount;

      for(let i = 0; i < countPages; i++){
        this.pages.push(i);
        this.pages[i] = [];
      }
      console.log(this.pages);

      //скрытие аннотации
      let element = document.getElementById("app");
      element = (element.querySelectorAll('[class="PSPDFKit-Container"]'))[0].firstChild.contentDocument.body;
      element = element.querySelectorAll('[class="PSPDFKit-8ehcbhz241z1tfyhjztbe12ube PSPDFKit-5hqvpgcgpf1769cn35dvtg4ktz PSPDFKit-Toolbar-Button-Shape-Annotation PSPDFKit-Toolbar-Button-Rectangle-Annotation PSPDFKit-yzpfzw9bfd9ejp1k7kgdacu7d PSPDFKit-Toolbar-Button PSPDFKit-Tool-Button"]');
      element[0].style.display = 'none';

      this.inst = instance;

      instance.addEventListener("annotations.create", (createdAnnotations) => {        
        if(this.flagDrawSecond == true){
          this.secondRectangle = createdAnnotations;
          this.drawValueVisibility = false;
          this.sendVisibility = true;
          this.flagDrawSecond = false;
        } else {
          this.firstRectangle = createdAnnotations;
        }
      });

      instance.addEventListener("annotations.delete", (deletedAnnotations) => {
        console.log(deletedAnnotations); 
        //удаление объектов по id

        try {

          this.nameVisibility = true;
          this.drawKeyVisibility = false;
          this.drawValueVisibility = false; 
          this.drawKeyVisibility = false;
          this.sendVisibility = false;

          this.arrayObjects.forEach((value, key) => { 
            if(value[0]._tail.array[0].id === deletedAnnotations._tail.array[0].id){
              this.arrayObjects.delete(key);
              this.inst.delete(value[1]._tail.array[0].id);
            }
            else if(value[1]._tail.array[0].id === deletedAnnotations._tail.array[0].id){
              this.arrayObjects.delete(key);
              this.inst.delete(value[0]._tail.array[0].id);
            }
          });

          this.message = "";
          this.createRectangle(1);

        } catch (error) {

          const { proxy,  } = Proxy.revocable(deletedAnnotations, {});
          
          this.arrayObjects.delete(this.message);

          if(this.firstRectangle !== 0 && proxy._tail.array[0].id !== this.firstRectangle._tail.array[0].id){
            this.inst.delete(this.firstRectangle);
            
          }
          if(this.secondRectangle !== 0 && proxy._tail.array[0].id !== this.secondRectangle._tail.array[0].id){
            this.inst.delete(this.secondRectangle);
          }

        }
      });

    },

    openDocument(event) {
      if (this.pdfFile && this.pdfFile.startsWith('blob:')) {
        window.URL.revokeObjectURL(this.pdfFile);
      }
      this.pdfFile = window.URL.createObjectURL(event.target.files[0]);
    },

    clickedOk: function (event) {
      if (event) {
        if(this.message == ""){
          console.log("no indent");
        } else {
          if (this.arrayObjects.has(this.message) == true){
            alert('Такая пара значений уже существует!');
          } 
          else {
            this.nameVisibility = false;
            this.drawKeyVisibility = true;
            this.createRectangle(); 
            this.arrayObjects.set(this.message, []);
            this.firstRectangle = 0;
            this.secondRectangle = 0;
          }
        }
      }
    },
    
    clickedCancel: function (event) {
      if (event) {
        this.nameVisibility = true;
        this.drawKeyVisibility = false;
        this.drawValueVisibility = false; 
        this.drawKeyVisibility = false;
        this.sendVisibility = false;

        //удаление объектов по id
        this.arrayObjects.forEach((value, key) => {
          if(key === this.message){
            this.arrayObjects.delete(key);
          }
        });
        this.message = "";
        if(this.firstRectangle != 0){
          this.inst.delete(this.firstRectangle._tail.array[0].id);
        }
        if(this.secondRectangle != 0){
          this.inst.delete(this.secondRectangle._tail.array[0].id);
        }
        this.createRectangle(1);
      }
    },

    clickSend: function (event) {
      if (event) {

        this.createRectangle(1);

        let elArr0 = this.arrayObjects.get(this.message);
        elArr0.push(this.firstRectangle);
        elArr0.push(this.secondRectangle);
        this.arrayObjects.set(this.message, elArr0);

        this.nameVisibility = true;
        this.drawKeyVisibility = false;
        this.drawValueVisibility = false; 
        this.drawKeyVisibility = false;
        this.sendVisibility = false;
        this.message='';
        this.firstRectangle = 0;
        this.secondRectangle = 0;
      }
    },

    clicks: function (event) {
      if (event) {
        if(this.firstRectangle != 0){
          this.createRectangle(2);
          this.drawKeyVisibility = false;
          this.drawValueVisibility = true;
          this.flagDrawSecond = true;
        }
        else{
          alert("Нарисуйте ключ!");
        }
      }
    },

    getJson: function (event) {
      if (event) {

        console.log(this.arrayObjects);
        console.log(this.inst.pageInfoForIndex(0));
        

        this.arrayObjects.forEach((value, key) => {
          if(key != undefined){
            alert(key); 
            let a = document.getElementById("app");
            let b = (a.querySelectorAll('[class="PSPDFKit-Container"]'));
            let c = b[0].firstChild;
            let d = c.contentDocument.body;

            let firstRec = value[0];
            firstRec = firstRec._tail.array[0].id
            a = d.querySelectorAll('[data-annotation-id="' + firstRec + '"]');
            b = a[0];
            let par = b.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement;
            let ind = par.getAttribute("data-page-index");
            console.log("indexPage: ", ind);
            b = b.firstChild.firstChild;
            console.log("x_top_left: ", b.getAttribute("x"));
            console.log("y_top_left: ", b.getAttribute("y"));
            console.log("width: ", b.getAttribute("width"));
            console.log("height: ", b.getAttribute("height"));
            console.log("");

            let secondRec = value[1];
            secondRec = secondRec._tail.array[0].id
            a = d.querySelectorAll('[data-annotation-id="' + secondRec + '"]');
            b = a[0];
            par = b.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement;
            ind = par.getAttribute("data-page-index");
            console.log("indexPage: ", ind);
            b = b.firstChild.firstChild;
            console.log("x_top_left: ", b.getAttribute("x"));
            console.log("y_top_left: ", b.getAttribute("y"));
            console.log("width: ", b.getAttribute("width"));
            console.log("height: ", b.getAttribute("height"));
          }
        });
      }
    },

    createRectangle: function(el){
      let a = document.getElementById("app");
      let b = (a.querySelectorAll('[class="PSPDFKit-Container"]'));
      let c = b[0].firstChild;
      let d = c.contentDocument.body;
      a = d.querySelectorAll('[class="PSPDFKit-8ehcbhz241z1tfyhjztbe12ube PSPDFKit-5hqvpgcgpf1769cn35dvtg4ktz PSPDFKit-Toolbar-Button-Shape-Annotation PSPDFKit-Toolbar-Button-Rectangle-Annotation PSPDFKit-yzpfzw9bfd9ejp1k7kgdacu7d PSPDFKit-Toolbar-Button PSPDFKit-Tool-Button"]');
      a[0].click();
      if(el == 1){
        a[0].click();
      }
      let divv = d.querySelectorAll('[class="PSPDFKit-Rectangle-Annotation-Toolbar PSPDFKit-6jgwts4kv8dr7cftse42cxk2z3"]');
      divv[0].style.display = 'none';
    },


  },
};
</script>

<style>

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  color: #2c3e50;
}

body {
  margin: 0;
}

input[type="file"] {
    display: none;
}

.custom-file-upload {
    border: 1px solid #ccc;
    border-radius: 4px;
    display: inline-block;
    padding: 6px 12px;
    cursor: pointer;
    background:#4A8FED;
    padding:10px;
    color:#fff;
    font:inherit;
    font-size: 16px;
    font-weight: bold;
}

.txt{
  font-size: 16pt;
}
.btn-txt{
  font-size: 16pt;
}

</style>