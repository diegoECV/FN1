import { Tabs } from 'expo-router';
import React from 'react';
import { HapticTab } from '@/components/haptic-tab';

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: '#0369A1',
        tabBarInactiveTintColor: '#94A3B8',
        headerShown: false,
        tabBarButton: HapticTab,
        tabBarStyle: { backgroundColor: '#fff', borderTopColor: '#E2E8F0' },
      }}>
      <Tabs.Screen
        name="index"
        options={{ title: 'Inicio', tabBarIcon: ({ color }) => <TabIcon icon="🏠" color={color} /> }}
      />
      <Tabs.Screen
        name="clients"
        options={{ title: 'Clientes', tabBarIcon: ({ color }) => <TabIcon icon="👥" color={color} /> }}
      />
      <Tabs.Screen
        name="providers"
        options={{ title: 'Proveedores', tabBarIcon: ({ color }) => <TabIcon icon="🏭" color={color} /> }}
      />
      <Tabs.Screen
        name="products"
        options={{ title: 'Productos', tabBarIcon: ({ color }) => <TabIcon icon="📦" color={color} /> }}
      />
    </Tabs>
  );
}

function TabIcon({ icon, color }: { icon: string; color: string }) {
  const { Text } = require('react-native');
  return <Text style={{ fontSize: 22, opacity: color === '#0369A1' ? 1 : 0.5 }}>{icon}</Text>;
}
