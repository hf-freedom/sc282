<template>
  <el-container style="height: 100vh">
    <el-header style="background: #2c3e50; color: white; display: flex; align-items: center; padding: 0 20px;">
      <h1 style="margin: 0; font-size: 24px;">农产品采摘配送系统</h1>
    </el-header>
    <el-container>
      <el-aside width="200px" style="background: #34495e; padding-top: 20px;">
        <el-menu
          :default-active="$route.path"
          router
          background-color="#34495e"
          text-color="#fff"
          active-text-color="#3498db"
        >
          <el-menu-item index="/">
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/batches">
            <span>批次管理</span>
          </el-menu-item>
          <el-menu-item index="/orders">
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/tasks">
            <span>采摘任务</span>
          </el-menu-item>
          <el-menu-item index="/delivery">
            <span>配送管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main style="background: #ecf0f1; padding: 20px;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', sans-serif;
}
</style>