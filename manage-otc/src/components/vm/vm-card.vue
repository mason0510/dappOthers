<template>
  <div :class="[type === 'horizantal' ? 'vm-card-horizantal' : 'vm-card-vertical' , 'vm-panel']">
    <div class="card-img">
      <img :src="img" alt="" @click="imgClicked" height="170">
      <div v-if="editable && type == 'vertical'" class="control">
        <span class="edit" @click="recommendClicked" v-if="hasRecommend">
          <!-- <a > -->

            <Icon v-if="isRecommend" type="thumbsdown" ></Icon>
            <Icon v-else type="thumbsup" ></Icon>
          <!-- </a> -->
        </span>
        <span class="delete" @click="deleteOk" v-if="hasDelete">
          <!-- <i class="fa fa-trash" @click="modalDelete=true"></i> -->
          <!-- <a > -->
            <Icon v-if="!isDelete" type="trash-a" ></Icon>
            <Icon v-else type="reply" ></Icon>
          <!-- </a> -->
        </span>
      </div>
    </div>
    <div class="card-desc panel-body">
      <h2>
        {{ title }}
      </h2>

    </div>
    <Modal
        v-model="modalDelete"
        class-name="big-model"
        title="Delete"
        ok-text="OK"
        cancel-text="Cancel"
        v-on:on-ok="deleteOk">
        Are you sure to delete this data?
    </Modal>
  </div>
</template>
<script>
  export default {
    name: 'VmCard',
    props: {
      type: {
        type: String,
        default: 'vertical'
      },
      editable: {
        type: Boolean,
        default: false
      },
      title: {
        type: String,
        default: 'Title'
      },
      hasDelete: {
        type: Boolean,
        default: true
      },
      hasRecommend: {
        type: Boolean,
        default: true
      },
      img: {
        type: String,
        default: require('@/assets/img/img-1.jpg')
      },
      desc: {
        type: String,
        default: 'Lorem Ipsum is simply dummy text of the printing and typesetting industry,Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s'
      },
      detailUrl: {
        type: String,
        default: '#'
      },
      editUrl: {
        type: String,
        default: '#'
      },
      isRecommend: {
        type: Boolean,
        default: false
      },
      isDelete: {
        type: Boolean,
        default: false
      },
      id: {
        type: String,
        default: ''
      }
    },
    data: function () {
      return {
        modalDelete: false
      }
    },
    methods: {
      deleteOk: function () {
        this.$emit('delete-ok')
      },
      imgClicked:function(){
        this.$emit('imgClicked')
      },
      recommendClicked:function(){
        this.$emit('recommendClicked')
      }
    }
  }
</script>
