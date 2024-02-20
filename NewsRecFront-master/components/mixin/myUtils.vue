<template>

</template>

<script>
  import * as utils from "../../utils"

  let _ = require("lodash");
  export default {
    name: "utils",
    data: function () {
      return {

      }
    },
    computed: {
      $_: function () {
        return _
      },
      $utils: function () {
        return utils
      }
    },
  }
</script>

<style scoped>

</style>
