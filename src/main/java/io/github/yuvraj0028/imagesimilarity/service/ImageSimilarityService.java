package io.github.yuvraj0028.imagesimilarity.service;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import io.github.yuvraj0028.imagesimilarity.bktree.BKTree;
import io.github.yuvraj0028.imagesimilarity.models.HashType;
import io.github.yuvraj0028.imagesimilarity.utils.HashUtils;

import java.awt.image.BufferedImage;

public class ImageSimilarityService {

    private final Map<HashType, Map<Long, String>> store = new EnumMap<>(HashType.class);
    private final Map<HashType, BKTree> treeCache = new EnumMap<>(HashType.class);

    public ImageSimilarityService() {
        for (HashType hashType : HashType.values()) {
            store.put(hashType, new HashMap<>());
        }
    }

    public long computeAndStore(File imageFile, HashType hashType) throws IOException {
        BufferedImage img = ImageIO.read(imageFile);
        long hashValue = computeHash(img, hashType);

        store.get(hashType).put(hashValue, imageFile.getName());

        BKTree tree = treeCache.get(hashType);

        if (tree != null) {
            tree.add(hashValue);
        } else {
            tree = new BKTree();
            for (Long hash : store.get(hashType).keySet()) {
                tree.add(hash);
            }
            treeCache.put(hashType, tree);
        }

        return hashValue;
    }

    public long computeHash(BufferedImage img, HashType hashType) {
        switch (hashType) {
            case PHASH: return HashUtils.pHash64(img);
            case DHASH: return HashUtils.dHash64(img);
            case BLOCKHASH: return HashUtils.blockHash64(img);
            default: throw new RuntimeException("HashType not supported");
        }
    }

    private BKTree getOrBuildTree(HashType type) {
        BKTree tree = treeCache.get(type);
        if (tree != null) return tree;

        tree = new BKTree();
        for (Long hash : store.get(type).keySet()) {
            tree.add(hash);
        }
        treeCache.put(type, tree);
        return tree;
    }

    public List<Long> findSimilar(long hashValue, HashType type, int maxDistance) {
        BKTree tree = getOrBuildTree(type);
        return tree.search(hashValue, maxDistance);
    }

    public List<String> findSimilar(File imageFile, HashType type, int maxDistance) throws IOException {
        BufferedImage img = ImageIO.read(imageFile);
        long hashValue = computeHash(img, type);

        List<Long> matches = findSimilar(hashValue, type, maxDistance);

        List<String> results = new ArrayList<>();
        Map<Long, String> map = store.get(type);

        for (Long h : matches) {
            String fileName = map.get(h);
            if (fileName != null) {
                results.add(fileName);
            }
        }

        return results;
    }

    public void clearAll() {
        store.values().forEach(Map::clear);
        treeCache.clear();
    }

    public void clearTreeCache() {
        treeCache.clear();
    }
}
