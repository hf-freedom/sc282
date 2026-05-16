import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import BatchView from '../views/BatchView.vue'
import OrderView from '../views/OrderView.vue'
import TaskView from '../views/TaskView.vue'
import DeliveryView from '../views/DeliveryView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/batches',
    name: 'batches',
    component: BatchView
  },
  {
    path: '/orders',
    name: 'orders',
    component: OrderView
  },
  {
    path: '/tasks',
    name: 'tasks',
    component: TaskView
  },
  {
    path: '/delivery',
    name: 'delivery',
    component: DeliveryView
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router