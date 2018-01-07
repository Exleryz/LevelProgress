# LevelProgress
本Demo来自简书https://www.jianshu.com/p/9e774990bdb5
对大佬的分享进行学习与改进
int accurateEnd = reachedPartEnd - progressHeight / 2;
int accurateStart = 0 + progressHeight / 2;
if (accurateEnd > accurateStart) {
    canvas.drawLine(accurateStart, lineY, accurateEnd, lineY, mPaint);
} else {
    canvas.drawLine(accurateStart, lineY, accurateStart, lineY, mPaint);
}
