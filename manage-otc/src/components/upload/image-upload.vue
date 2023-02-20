<style lang="less">
  @import './image-editor.less';
  @import './upload.less';
</style>
<style lang="less">

    .ivu-modal{
      height: 600px;
      z-index: 900;
    }

</style>
<template>

  <div>
    <div style="float: left">
      <Upload
      :before-upload="handleUpload"
      :action="uploadUrl"
      @on-progress="onProgress"
      @on-success="onSuccess"
      @on-error="onError">
      <Button type="ghost" icon="ios-cloud-upload-outline">选择要上传文件的文件</Button>
    </Upload>
     <Upload
      :before-upload="handleUploadCut"
      :action="uploadUrl"
      @on-progress="onProgress"
      @on-success="onSuccess"
      @on-error="onError">
      <Button type="ghost" icon="ios-cloud-upload-outline">剪切上传</Button>
    </Upload>
    <Button type="primary" v-show="files.length > 0" icon="upload" @click="upload" :loading="loadingStatus">{{ loadingStatus ? '上传中' : '点击上传' }}</Button>
  </div>
    <div style="float: left;margin-left:10px;">
      <div class="admin-upload-list" v-for="(item,index) in files" :key="index">
                                            <template >
                                                <img :src="item.file">
                                                <div class="admin-upload-list-cover">
                                                    <Icon type="ios-eye-outline" @click.native="handleView(item)"></Icon>
                                                    <Icon type="ios-trash-outline" @click.native="handleRemove(item)"></Icon>
                                                </div>
                                            </template>

                                        </div>

    </div>
<div style="clear: both;"></div>
      <Modal v-model="showCroped"   :transfer="false">
        <p slot="header">图片</p>

        <div class="image-editor">
          <Card>
                    <Row :gutter="10">
                        <Col span="24" class="image-editor-con1">
                            <div class="cropper">
                                <img :id="cropimg" alt="">
                                <div :id="preview" ></div>
                            </div>
                        </Col>
                    </Row>
                </Card>
        </div>


        <!-- <Row class="image-editor-con1">
        <div class="cropper">
          <img id="cropimg1" alt="">
          <div id="preview1" ></div>
        </div>
        </Row> -->
        <!--<Row :gutter="10">-->
          <!--<Col span="14" class="image-editor-con1">-->
         <!---->
          <!--</Col>-->
          <!--<Col span="10" class="image-editor-con1">-->
          <!--<Row type="flex" justify="center" align="middle" class="image-editor-con1-preview-con">-->
            <!---->
          <!--</Row>-->
          <!--<div class="image-editor-con1-btn-con margin-top-10">-->
            <!--&lt;!&ndash;<span><Button @click="handlecrop1" type="primary" icon="crop">裁剪</Button></span>&ndash;&gt;-->
          <!--</div>-->

          <!--</Col>-->
        <!--</Row>-->

      <div slot="footer">
        <Button   @click="handelCancel">取消</Button>
        <Button type="primary"  @click="handlecrop" icon="crop">裁剪</Button>
      </div>
    </Modal>

    <Modal v-model="option1.showCropedImage" :transfer="false">
      <p slot="header">预览裁剪之后的图片</p>
      <img :src="option1.cropedImg" alt="" v-if="option1.showCropedImage" style="width: 100%;">
    </Modal>
  </div>



</template>
<script>
  import Cropper from 'cropperjs';
 import {upload,uploadInfo} from '../../api/system'

  export default {
    props: {
      uploadUrl : String,
      uploadType : Number,
      files:{
      	type:Array,
      	default:function  () {
      		return []
      	}
      },
      cropimg:{
      	type:String,
      	default:'cropimg1'
      },
       preview:{
      	type:String,
      	default:'preview1'
      },
    },
    data () {
      return {
        cropper1: {},
        option1: {
          showCropedImage: false,
          cropedImg: ''
        },
        file: null,
        fileName:'',
        loadingStatus: false,
        showCroped:false,
        uploadList:[]
      };
    },
    methods: {
    	handleUploadCut(file){
    		 this.file = file;
         let reader = new FileReader();
         reader.onload = () => {
           this.cropper1.replace(reader.result);
           reader.onload = null;
         };
         reader.readAsDataURL(this.file);
         this.showCroped=true;
          return false;
    	},
      handleUpload (file) {
			this.file = file;
       let reader = new FileReader();
        let that = this;
        reader.onload=function(e){
          let fileName = that.file.name;
          let file = e.target.result;
          let id= that.randomNum(10);
          let uploadFile = {fileName:fileName,file:file,id:id};
          that.files.push(uploadFile);
        };
        reader.readAsDataURL(this.file);
        //this.showCroped=true;
        return false;
      },
      upload () {
        // this.loadingStatus = true;
        // setTimeout(() => {
        //   this.file = null;
        //   this.loadingStatus = false;
        //   this.$Message.success('上传成功')
        // }, 1500);

        // let param = new FormData(); //创建form对象
        // param.append('file',this.files[0].file);//通过append向form对象添加数据
        // param.append('uploadType','3');//添加form表单中其他数据

        let fileNum = this.files.length;
        let sucessFiles = [];
        console.log(this.files)
        this.loadingStatus = true;
        for(var index in this.files){
          let file = this.files[index].file;
          let param = {
            file:file,
            uploadType:this.uploadType
          };
          // let formdata = new FormData();
          // formdata.append('file',file);
          // formdata.append('uploadType',this.uploadType);

          uploadInfo(param).then((res) => {
              fileNum --;

              if(res.status==200){
                if(res.data.code==0){
                  sucessFiles.push(index);
                  this.$emit('onUploaded',res.data.data);
                }else{
                  if(res.data.Message){
                    this.$Message.error(res.data.Message);
                  }
                }
              }else{
                this.$Message.error('上传失败!');
              }

              if(fileNum<1){
/*
                sucessFiles.sort(this.NumDescSort);
                for(var i in sucessFiles){
                  this.files.splice(i,1);
                }*/
								this.files.length=0
                this.loadingStatus = false;
              }
          });
        }



      },
      NumDescSort(a,b)
      {
       return b - a;
     },
      //删除预览
      handleRemove(item){
        for (var index in this.files) {
          if(item.id==this.files[index].id){
            this.files.splice(index, 1);
            break;
          }
        }
      },
      //预览
      handleView(item){
        this.option1.cropedImg = item.file;
        this.option1.showCropedImage = true;
      },
      randomNum(n){
        var t='';
        for(var i=0;i<n;i++){
        t+=Math.floor(Math.random()*10);
        }
        return t;
      },
      handlecrop () {

        let fileName = this.file.name;

        let file = this.cropper1.getCroppedCanvas().toDataURL();
        let id= this.randomNum(10);
        let uploadFile = {fileName:fileName,file:file,id:id};
        this.files.push(uploadFile);

        /********
        this.option1.cropedImg = file;

        this.file = this.option1.cropedImg;//file;
        //this.option1.showCropedImage = true;
        *********/
        this.showCroped=false;

      },
      handelCancel(){
        this.showCroped=false;
      },
      handlepreview(){
        this.option1.showCropedImage = true;
      },
      onProgress(event, file, fileList){
        this.$emit('onProgress',event, file, fileList);
      },
      onSuccess(response, file, fileList){
        this.$emit('onSuccess',response, file, fileList);
      },
      onError(error, file, fileList){
        this.$emit('onError',error, file, fileList);
      },


    },
    mounted () {
      let img1 = document.getElementById(this.cropimg);
      this.cropper1 = new Cropper(img1, {
        dragMode: 'move',
        preview: '#preview1',
        restore: false,
        center: false,
        highlight: false,
        cropBoxMovable: false,
        toggleDragModeOnDblclick: false
      });

    }
  };
</script>
<style>
	/*!
 * Cropper.js v1.3.4
 * https://github.com/fengyuanchen/cropperjs
 *
 * Copyright (c) 2015-2018 Chen Fengyuan
 * Released under the MIT license
 *
 * Date: 2018-03-31T06:49:06.196Z
 */

.cropper-container {
  direction: ltr;
  font-size: 0;
  line-height: 0;
  position: relative;
  -ms-touch-action: none;
  touch-action: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}

.cropper-container img {/*Avoid margin top issue (Occur only when margin-top <= -height)
 */
  display: block;
  height: 100%;
  image-orientation: 0deg;
  max-height: none !important;
  max-width: none !important;
  min-height: 0 !important;
  min-width: 0 !important;
  width: 100%;
}

.cropper-wrap-box,
.cropper-canvas,
.cropper-drag-box,
.cropper-crop-box,
.cropper-modal {
  bottom: 0;
  left: 0;
  position: absolute;
  right: 0;
  top: 0;
}

.cropper-wrap-box,
.cropper-canvas {
  overflow: hidden;
}

.cropper-drag-box {
  background-color: #fff;
  opacity: 0;
}

.cropper-modal {
  background-color: #000;
  opacity: .5;
}

.cropper-view-box {
  display: block;
  height: 100%;
  outline-color: rgba(51, 153, 255, 0.75);
  outline: 1px solid #39f;
  overflow: hidden;
  width: 100%;
}

.cropper-dashed {
  border: 0 dashed #eee;
  display: block;
  opacity: .5;
  position: absolute;
}

.cropper-dashed.dashed-h {
  border-bottom-width: 1px;
  border-top-width: 1px;
  height: 33.33333%;
  left: 0;
  top: 33.33333%;
  width: 100%;
}

.cropper-dashed.dashed-v {
  border-left-width: 1px;
  border-right-width: 1px;
  height: 100%;
  left: 33.33333%;
  top: 0;
  width: 33.33333%;
}

.cropper-center {
  display: block;
  height: 0;
  left: 50%;
  opacity: .75;
  position: absolute;
  top: 50%;
  width: 0;
}

.cropper-center:before,
.cropper-center:after {
  background-color: #eee;
  content: ' ';
  display: block;
  position: absolute;
}

.cropper-center:before {
  height: 1px;
  left: -3px;
  top: 0;
  width: 7px;
}

.cropper-center:after {
  height: 7px;
  left: 0;
  top: -3px;
  width: 1px;
}

.cropper-face,
.cropper-line,
.cropper-point {
  display: block;
  height: 100%;
  opacity: .1;
  position: absolute;
  width: 100%;
}

.cropper-face {
  background-color: #fff;
  left: 0;
  top: 0;
}

.cropper-line {
  background-color: #39f;
}

.cropper-line.line-e {
  cursor: ew-resize;
  right: -3px;
  top: 0;
  width: 5px;
}

.cropper-line.line-n {
  cursor: ns-resize;
  height: 5px;
  left: 0;
  top: -3px;
}

.cropper-line.line-w {
  cursor: ew-resize;
  left: -3px;
  top: 0;
  width: 5px;
}

.cropper-line.line-s {
  bottom: -3px;
  cursor: ns-resize;
  height: 5px;
  left: 0;
}

.cropper-point {
  background-color: #39f;
  height: 5px;
  opacity: .75;
  width: 5px;
}

.cropper-point.point-e {
  cursor: ew-resize;
  margin-top: -3px;
  right: -3px;
  top: 50%;
}

.cropper-point.point-n {
  cursor: ns-resize;
  left: 50%;
  margin-left: -3px;
  top: -3px;
}

.cropper-point.point-w {
  cursor: ew-resize;
  left: -3px;
  margin-top: -3px;
  top: 50%;
}

.cropper-point.point-s {
  bottom: -3px;
  cursor: s-resize;
  left: 50%;
  margin-left: -3px;
}

.cropper-point.point-ne {
  cursor: nesw-resize;
  right: -3px;
  top: -3px;
}

.cropper-point.point-nw {
  cursor: nwse-resize;
  left: -3px;
  top: -3px;
}

.cropper-point.point-sw {
  bottom: -3px;
  cursor: nesw-resize;
  left: -3px;
}

.cropper-point.point-se {
  bottom: -3px;
  cursor: nwse-resize;
  height: 20px;
  opacity: 1;
  right: -3px;
  width: 20px;
}

@media (min-width: 768px) {
  .cropper-point.point-se {
    height: 15px;
    width: 15px;
  }
}

@media (min-width: 992px) {
  .cropper-point.point-se {
    height: 10px;
    width: 10px;
  }
}

@media (min-width: 1200px) {
  .cropper-point.point-se {
    height: 5px;
    opacity: .75;
    width: 5px;
  }
}

.cropper-point.point-se:before {
  background-color: #39f;
  bottom: -50%;
  content: ' ';
  display: block;
  height: 200%;
  opacity: 0;
  position: absolute;
  right: -50%;
  width: 200%;
}

.cropper-invisible {
  opacity: 0;
}

.cropper-bg {
  background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQAQMAAAAlPW0iAAAAA3NCSVQICAjb4U/gAAAABlBMVEXMzMz////TjRV2AAAACXBIWXMAAArrAAAK6wGCiw1aAAAAHHRFWHRTb2Z0d2FyZQBBZG9iZSBGaXJld29ya3MgQ1M26LyyjAAAABFJREFUCJlj+M/AgBVhF/0PAH6/D/HkDxOGAAAAAElFTkSuQmCC');
}

.cropper-hide {
  display: block;
  height: 0;
  position: absolute;
  width: 0;
}

.cropper-hidden {
  display: none !important;
}

.cropper-move {
  cursor: move;
}

.cropper-crop {
  cursor: crosshair;
}

.cropper-disabled .cropper-drag-box,
.cropper-disabled .cropper-face,
.cropper-disabled .cropper-line,
.cropper-disabled .cropper-point {
  cursor: not-allowed;
}

</style>