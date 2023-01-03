package com.mxc.merkle.model;

import com.mxc.merkle.util.BigDecimalUtils;
import com.mxc.merkle.util.SignatureUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

public class MerklePathNode {
    /**
     * 层级
     */
    private Integer level;
    /**
     * 当前节点类型：0/根节点 1/左节点 2/右节点
     */
    private Integer type;
    /**
     * 节点hash
     */
    private String hash;
    /**
     * 默克尔资产：key=币种 value= 金额
     */
    private SortedMap<String, String> balances;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Map<String, String> getBalances() {
        return balances;
    }

    public void setBalances(SortedMap<String, String> balances) {
        this.balances = balances;
    }

    public void mergeBalance(MerklePathNode merklePathNode) {
        if (Objects.isNull(merklePathNode) || Objects.isNull(merklePathNode.getBalances()) || merklePathNode.getBalances().size() == 0) {
            return;
        }
        merklePathNode.getBalances().forEach((key, value) -> {
            String existValue = this.balances.get(key);
            BigDecimal sumBigDecimal = BigDecimalUtils.safeSumBigDecimal(BigDecimalUtils.parseString(existValue), BigDecimalUtils.parseString(value));
            this.balances.put(key, sumBigDecimal.toPlainString());
        });
    }

    /**
     * 计算节点hashId
     *
     * @param leftHash  左节点hash
     * @param rightHash 右节点hash
     * @return 计算出来的hashId
     */
    public String calcHashId(String leftHash, String rightHash) {
        StringBuilder balanceSb = new StringBuilder();
        this.getBalances().forEach((token, balance) -> balanceSb.append(token).append(":").append(BigDecimalUtils.getBigDecimalPlainStr(balance)).append(","));
        if (balanceSb.length() > 1) {
            balanceSb.setLength(balanceSb.length() - 1);
        }
        String content = leftHash + "," + rightHash + "," + balanceSb;
        String hashId = SignatureUtil.genHashId(content);
        this.setHash(hashId);
        return hashId;
    }

}
