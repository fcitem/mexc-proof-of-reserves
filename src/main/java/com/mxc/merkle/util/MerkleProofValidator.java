package com.mxc.merkle.util;

import com.mxc.merkle.model.MerklePathNode;
import com.mxc.merkle.model.MerkleSelfNode;
import com.mxc.merkle.model.MerkleTree;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MerkleProofValidator {

    public static boolean validate(MerkleTree merkleTree) {
        if (Objects.isNull(merkleTree)) {
            throw new IllegalArgumentException("merkleTree not null");
        }
        List<MerklePathNode> pathNodeList = merkleTree.getPathList();
        if (Objects.isNull(merkleTree.getSelf()) || Objects.isNull(pathNodeList) || pathNodeList.size() == 0) {
            throw new IllegalArgumentException("invalid merkleTree content");
        }
        //自节点验证
        if (!merkleTree.getSelf().validate()) {
            System.out.println("self validate failure");
            return false;
        }
        //路径验证
        if (!pathValidate(merkleTree.getPathList(), merkleTree.getSelf())) {
            System.out.println("path validate failure");
            return false;
        }
        return true;
    }

    private static boolean pathValidate(List<MerklePathNode> pathNodeList, MerkleSelfNode selfNode) {
        MerklePathNode rootNode = pathNodeList.get(pathNodeList.size() - 1);
        //获取深度
        int depth = rootNode.getLevel();
        Pair<MerklePathNode, MerklePathNode> nodePair = null;
        //key=level，value=数据
        Map<Integer, MerklePathNode> levelNodeMap = pathNodeList.stream().collect(Collectors.toMap(MerklePathNode::getLevel, merklePathNode -> merklePathNode));
        //通过左右节点计算出来的内部节点
        MerklePathNode clcInnerNode = selfNode;
        for (int i = 0; i < depth; i++) {
            MerklePathNode node = levelNodeMap.get(i);
            if (Objects.isNull(node)) {
                //创建一个空节点
                node = createEmptyNode(clcInnerNode.getType() == 1 ? 2 : 1, clcInnerNode.getLevel());
            }
            nodePair = decideNodePair(clcInnerNode, node);
            int parentLevel = i + 1;
            //通过左右节点计算内部父节点
            clcInnerNode = createInnerNode(nodePair.getLeft(), nodePair.getRight(), getParentNodeType(levelNodeMap.get(parentLevel)), parentLevel);
        }
        if (Objects.isNull(nodePair)) {
            System.out.println("validate error");
            return false;
        }
        MerklePathNode clcRootNode = createInnerNode(nodePair.getLeft(), nodePair.getRight(), 1, rootNode.getLevel());
        return clcRootNode.getHash().equals(rootNode.getHash());
    }

    private static Pair<MerklePathNode, MerklePathNode> decideNodePair(MerklePathNode currentNode, MerklePathNode brotherNode) {
        return currentNode.getType() == 1 ? Pair.of(currentNode, brotherNode) : Pair.of(brotherNode, currentNode);
    }

    private static int getParentNodeType(MerklePathNode parentNodeBrother) {
        //说明右节点为空
        if (Objects.isNull(parentNodeBrother)) {
            return 1;
        } else {
            return parentNodeBrother.getType() == 1 ? 2 : 1;
        }
    }

    public static MerklePathNode createInnerNode(MerklePathNode leftNode, MerklePathNode rightNode, int type, int level) {
        MerklePathNode merklePathNode = new MerklePathNode();
        merklePathNode.setBalance(new TreeMap<>());
        merklePathNode.mergeBalance(leftNode);
        //右节点不为空
        if (StringUtils.isNotBlank(rightNode.getHash())) {
            initBalanceData(merklePathNode);
            merklePathNode.mergeBalance(rightNode);
        }
        merklePathNode.setLevel(level);
        merklePathNode.setType(type);
        //计算hash
        merklePathNode.calcHashId(leftNode.getHash(), rightNode.getHash());
        return merklePathNode;
    }

    public static MerklePathNode createEmptyNode(int type, int level) {
        MerklePathNode merklePathNode = new MerklePathNode();
        merklePathNode.setBalance(new TreeMap<>());
        merklePathNode.setLevel(level);
        merklePathNode.setType(type);
        merklePathNode.setHash("");
        return merklePathNode;
    }

    private static void initBalanceData(MerklePathNode merklePathNode) {
        merklePathNode.getBalance().putIfAbsent("BTC", BigDecimal.ZERO.toPlainString());
        merklePathNode.getBalance().putIfAbsent("USDT", BigDecimal.ZERO.toPlainString());
        merklePathNode.getBalance().putIfAbsent("ETH", BigDecimal.ZERO.toPlainString());
        merklePathNode.getBalance().putIfAbsent("USDC", BigDecimal.ZERO.toPlainString());
    }
}
