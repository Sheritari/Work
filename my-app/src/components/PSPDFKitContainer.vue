<template>
  <div class="pdf-container"></div>
</template>

<script>
import PSPDFKit from "pspdfkit";

/**
 * PSPDFKit for Web example component.
 */
export default {
  data() {
    return {
      inst: '',
    };
  },

  name: 'PSPDFKit',
  /**
	 * The component receives `pdfFile` as a prop, which is type of `String` and is required.
	 */
  props: {
    pdfFile: {
      type: String,
      required: true,
    },
  },
  
  /**
	* We wait until the template has been rendered to load the document into the library.
	*/
  
  mounted() {

    this.loadPSPDFKit().then(instance => {
      const items = instance.toolbarItems;
      this.inst = instance;
      // Hide the toolbar item with the `id` "ink"
      // by removing it from the array of items.
      instance.setToolbarItems(items.filter((item) =>  item.type == "pager" || item.type == "rectangle"));
      PSPDFKit.defaultAnnotationPresets.rectangle.strokeWidth = 1;
      // console.log(PSPDFKit.defaultEditableAnnotationTypes[8]);
      // console.log(PSPDFKit.Annotation);
      const annotation = new PSPDFKit.Annotations.RectangleAnnotation({
        pageIndex: 0,
        boundingBox: new PSPDFKit.Geometry.Rect({
          left: 10,
          top: 10,
          width: 100,
          height: 100,
        }),
        cloudyBorderIntensity: 2,
        cloudyBorderInset: new PSPDFKit.Geometry.Inset({
          left: 9,
          top: 9,
          right: 9,
          bottom: 9,
        })
      });
      annotation.id = '13';
      console.log(annotation.id);  

      this.$emit("loaded", instance);
      
    }
    );
    // this.loadPSPDFKit({annotationPresets}).then((instance) => { 
    //   const defaultItems = PSPDFKit.defaultToolbarItems;
    //   console.log(defaultItems);
    //   const items = instance.toolbarItems;
    //   this.inst = instance;
    //   // Hide the toolbar item with the `id` "ink"
    //   // by removing it from the array of items.
    //   instance.setToolbarItems(items.filter((item) =>  item.type == "pager" || item.type == "rectangle"));
    //   this.$emit("loaded", instance);

    // });
  },
  /**
	 * We watch for `pdfFile` prop changes and trigger unloading and loading when there's a new document to load.
	 */
  watch: {
    pdfFile(val) {
      if (val) {
        this.loadPSPDFKit();
      }
    },
  },
  /**
	 * Our component has the `loadPSPDFKit` method. This unloads and cleans up the component and triggers document loading.
	 */
  methods: {
    async loadPSPDFKit() {
      PSPDFKit.unload(".pdf-container");
      return PSPDFKit.load({
        // access the pdfFile from props
        document: this.pdfFile,
        container: ".pdf-container",
      });
    },
  },

  /**
	 * Clean up when the component is unmounted so it's ready to load another document (not needed in this example).
	 */
  beforeUnmount() {
    PSPDFKit.unload(".pdf-container");
  },
};
</script>


<style scoped>
.pdf-container {
  height: 100vh;
}
</style>