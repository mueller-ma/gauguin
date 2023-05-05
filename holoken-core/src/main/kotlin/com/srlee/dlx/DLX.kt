package com.srlee.dlx

open class DLX(
    numberOfColumns: Int,
    numberOfNodes: Int
) {
    private val root = DLXColumn()
    private val trysolution = ArrayList<Int>()
    private var ColHdrs: Array<DLXColumn> = Array(numberOfColumns + 1) { DLXColumn() }
    private var Nodes: Array<DLXNode?> = arrayOfNulls(numberOfNodes + 1)
    private var numnodes = 0
    private var lastNodeAdded: DLXNode? = null
    private var numberOfSolutions = 0
    private var previousRow = -1
    private var solvetype: SolveType? = null

    init {
        var prev: DLXColumn? = root
        for (i in 1..numberOfColumns) {
            prev!!.right = ColHdrs[i]
            ColHdrs[i].left = prev
            prev = ColHdrs[i]
        }
        root.left = ColHdrs[numberOfColumns]
        ColHdrs[numberOfColumns].right = root
    }

    private fun coverColumn(column: DLXColumn) {
        column.right!!.left = column.left
        column.left!!.right = column.right
        var i = column.down
        while (i !== column) {
            var j = i!!.right
            while (j !== i) {
                j!!.down!!.up = j.up
                j.up!!.down = j.down
                (j as DLXNode).column.decrementSize()
                j = j.right
            }
            i = i.down
        }
    }

    private fun uncoverColumn(column: DLXColumn) {
        var i = column.up
        while (i !== column) {
            var j = i!!.left
            while (j !== i) {
                (j as DLXNode).column.incrementSize()
                j.down!!.up = j
                j.up!!.down = j
                j = j.left
            }
            i = i.up
        }
        column.right!!.left = column
        column.left!!.right = column
    }

    private fun ChooseMinCol(): DLXColumn? {
        var minsize = Int.MAX_VALUE
        var search = root.right as DLXColumn
        var mincol = search

        while (search !== root) {
            if (search.size < minsize) {
                mincol = search
                minsize = mincol.size
                if (minsize == 0) {
                    break
                }
            }
            search = search.right as DLXColumn
        }

        return if (minsize == 0) {
            null
        } else {
            mincol
        }
    }

    fun addNode(column: Int, row: Int) {
        val node = DLXNode(ColHdrs[column], row)
        Nodes[++numnodes] = node

        if (previousRow == row) {
            node.left = lastNodeAdded
            node.right = lastNodeAdded!!.right
            lastNodeAdded!!.right = node
            node.right!!.left = node
        } else {
            previousRow = row
            node.left = node
            node.right = node
        }
        lastNodeAdded = node
    }

    fun Solve(st: SolveType): Int {
        solvetype = st
        numberOfSolutions = 0
        search(trysolution.size)
        return numberOfSolutions
    }

    private fun search(k: Int) {
        if (root.right === root) {
            numberOfSolutions++
            return
        }
        val chosenCol = ChooseMinCol()
        if (chosenCol != null) {
            coverColumn(chosenCol)
            var r = chosenCol.down
            while (r !== chosenCol) {
                if (k >= trysolution.size) {
                    trysolution.add((r as DLXNode).row)
                } else {
                    trysolution[k] = (r as DLXNode).row
                }
                var j = r.right
                while (j !== r) {
                    coverColumn((j as DLXNode).column)
                    j = j.right
                }
                search(k + 1)
                if (solvetype == SolveType.ONE && numberOfSolutions > 0) // Stop as soon as we find 1 solution
                {
                    return
                }
                if (solvetype == SolveType.MULTIPLE && numberOfSolutions > 1) // Stop as soon as we find multiple solutions
                {
                    return
                }
                j = r.left
                while (j !== r) {
                    uncoverColumn((j as DLXNode).column)
                    j = j.left
                }
                r = r.down
            }
            uncoverColumn(chosenCol)
        }
    }

    enum class SolveType {
        ONE, MULTIPLE
    }
}