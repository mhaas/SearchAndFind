

package de.haas.searchandfind.frontend;

/**
 *
 * @author laga
 */
public class QueryResult {

    private String filePath;
    private float queryScore;


    public QueryResult(String path, float score) {
        this.filePath = path;
        this.queryScore = score;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the score
     */
    public float getScore() {
        return queryScore;
    }

    /**
     * @param score the score to set
     */
    public void setScore(float score) {
        this.queryScore = score;
    }

    public String toString() {
        return this.getScore() + ": " + this.getFilePath();
    }

}
