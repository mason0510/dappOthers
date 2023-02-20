
<template>

  <div>
    <Modal v-model="isShowImage" @on-cancel="closePreview" >
      <p slot="header">图片</p>
      <img :src="imageUrl"  style="width: 100%;">
      <div slot="footer">
        <Button type="primary"  @click="closePreview">关闭</Button>
      </div>
    </Modal>
  </div>



</template>
<script>


  export default {
    props: {
      imageUrl : String,
      imageShow : Boolean
    },
    data () {
      return {
        isShowImage:this.imageShow
      };
    },
    watch: {
        imageShow(val) {
            this.isShowImage = val;
        }
    },
    methods: {
      closePreview(){
        this.$emit('closePreview');
      }
    }
  };
</script>
