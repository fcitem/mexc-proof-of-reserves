import com.alibaba.fastjson.JSON;
import com.mexc.merkle.model.MerkleTree;
import com.mexc.merkle.util.MerkleProofValidator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class MexcMerkleValidatorTest {

    private static final String TEST_PROOF_PATH = Objects.requireNonNull(MexcMerkleValidatorTest.class.getClassLoader().getResource("merkle_proof.json")).getPath();

    @Test
    public void test_validator() {
        String jsonFile = TEST_PROOF_PATH;
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(jsonFile));
        } catch (IOException e) {
            throw new RuntimeException("can not found json file: " + jsonFile);
        }
        String pathJson = new String(bytes);
        MerkleTree merkleTree;
        try {
            merkleTree = JSON.parseObject(pathJson, MerkleTree.class);
        } catch (Exception e) {
            throw new RuntimeException("json content is inValid");
        }
        boolean validate = MerkleProofValidator.validate(merkleTree);
        Assert.assertTrue(validate);
    }

}
