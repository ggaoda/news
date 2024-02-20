<template>

</template>

<script>
import ApiHelper from "@/API/ApiHelper";

export default {
  name: "LabelApi",
  extends: ApiHelper,
  data() {
    return {
      baseUrl: "/v1/labels",
    }
  },
  methods: {
    getLabelsApi() {
      return this.get(this.baseUrl + "/labels", {num: 8})
    },
    setUserLabelsApi(labels) {
      return this.post(this.baseUrl + "/setUserLabels", labels, {'Content-Type': 'application/json'})
    }
  }
}
</script>

<style scoped>

</style>
