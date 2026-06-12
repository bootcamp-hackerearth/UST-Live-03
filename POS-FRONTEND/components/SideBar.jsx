"use client"
import React, { useState, useEffect } from 'react'
import axios from "./axiosConfig"
import { useRouter, usePathname } from 'next/navigation'
import { 
  Warehouse, ShelvingUnit, Users, LayoutList, Boxes, 
  PersonStanding, Package, UserKey, Ruler, 
  AlignVerticalDistributeEnd, ChartNetwork, Tag, 
  CircleDollarSign, Tags 
} from "lucide-react"

const SideBar = () => {
  const [nodes, setNodes] = useState([])
  const router = useRouter()
  const currentPathname = usePathname()

  useEffect(() => {
    const fetchNodes = async () => {
      try {
          const res = await axios.get("/node/getNodesForRoles")
          setNodes(res.data || [])
      } catch (err) {
        console.error("Failed to load navigation nodes", err)
      }
    }
    fetchNodes()
  }, [])

  const iconRegistry = {
    "Warehouse": <Warehouse className="size-4 shrink-0" />,
    "Shelfs": <ShelvingUnit className="size-4 shrink-0" />,
    "User": <Users className="size-4 shrink-0" />,
    "Category": <LayoutList className="size-4 shrink-0" />,
    "Stocks": <Boxes className="size-4 shrink-0" />,
    "Customer": <PersonStanding className="size-4 shrink-0" />,
    "Product": <Package className="size-4 shrink-0" />,
    "Roles": <UserKey className="size-4 shrink-0" />,
    "Unit": <Ruler className="size-4 shrink-0" />,
    "Racks": <AlignVerticalDistributeEnd className="size-4 shrink-0" />,
    "Nodes": <ChartNetwork className="size-4 shrink-0" />,
    "Brand": <Tag className="size-4 shrink-0" />,
    "Price": <CircleDollarSign className="size-4 shrink-0" />,
    "Models": <Tags className="size-4 shrink-0" />
  }

  const isNodeActive = (nodePath) => {
    if (!nodePath) return false
    return currentPathname.split("/")[1] === nodePath.split("/")[1]
  }

  return (
    <aside className="fixed left-0 top-0 h-full w-12 hover:w-52 bg-slate-950 border-r border-slate-800 text-slate-400 hover:text-slate-200 transition-all duration-300 ease-in-out pt-14 z-10 flex flex-col group/sidebar shadow-xl">
      
      <nav className="flex-1 px-2 space-y-0.5 overflow-y-auto scrollbar-none">
        <ul>
          {nodes.map((node) => {
            const isActive = isNodeActive(node.path)
            return (
              <li key={node.path || node.identifier} className="my-0.5">
                <button
                  onClick={() => router.push(node.path)}
                  className={`w-full flex items-center gap-3 px-2 py-2 rounded text-xs font-medium transition-all group duration-150 text-left cursor-pointer
                    ${isActive 
                      ? "bg-blue-600 text-white shadow" 
                      : "hover:bg-slate-900 hover:text-slate-100"
                    }`}
                >
                  <div className={isActive ? "text-white" : "text-slate-400 group-hover:text-slate-200"}>
                    {iconRegistry[node.identifier] || <Package className="size-4 shrink-0" />}
                  </div>

                  <span className="opacity-0 group-hover/sidebar:opacity-100 transition-opacity duration-200 tracking-wide font-medium truncate">
                    {node.identifier}
                  </span>
                </button>
              </li>
            )
          })}
        </ul>
      </nav>

    </aside>
  )
}

export default SideBar