// src/pages/PostDetail.js
import React, { useEffect, useState } from "react";

// 더미 샘플 데이터
const dummyPost = {
  id: 1,
  title: "아이템 제목",
  price: 50000,
  sellerNickname: "판매자",
  categoryDisplayName: "RPG",
  description: "아이템 상세 설명입니다.",
  imagePaths: [
    "https://picsum.photos/200?random=1",
    "https://picsum.photos/200?random=2",
  ],
  isAuthor: true, // 현재 로그인 유저가 글쓴이인지 여부
};

const currentUserId = 1; // 로그인된 유저 ID

// 댓글 더미 데이터 (페이지별)
const dummyCommentsPage = [
  {
    content: [
      { id: 1, writerNickname: "홍길동", writerId: 1, content: "좋은 글이네요", createdAt: "2025-07-05T15:30:21" },
      { id: 2, writerNickname: "임꺽정", writerId: 2, content: "관심있어요!", createdAt: "2025-07-05T15:31:55" },
    ],
    last: false,
  },
  {
    content: [
      { id: 3, writerNickname: "홍길동", writerId: 1, content: "댓글 테스트!", createdAt: "2025-07-05T15:32:11" },
    ],
    last: true,
  }
];

function PostDetail() {
  // 상태 정의
  const [post, setPost] = useState(dummyPost);
  const [comments, setComments] = useState([]);
  const [commentPage, setCommentPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [commentInput, setCommentInput] = useState("");
  const [loading, setLoading] = useState(false);

  // 댓글 페이징 로딩(실제 fetch는 API로)
  const loadComments = async (reset = false) => {
    setLoading(true);
    // 실제로는 fetch(`/api/comments/${post.id}?page=${reset ? 0 : commentPage}`)
    const pageData = dummyCommentsPage[reset ? 0 : commentPage];
    setComments(prev =>
        reset ? [...pageData.content] : [...prev, ...pageData.content]
    );
    setHasMore(!pageData.last);
    setCommentPage(prev => reset ? 1 : prev + 1);
    setLoading(false);
  };

  // 댓글 처음 로딩
  useEffect(() => {
    loadComments(true);
    // 실제로는 postId에 따라 post, comments 모두 fetch!
  }, [post.id]);

  // 댓글 작성
  const handleCommentSubmit = async e => {
    e.preventDefault();
    if (!commentInput.trim()) return;
    // 실제 POST: fetch(`/api/comments/${post.id}`, {method: "POST", ...})
    // 성공하면 댓글 새로고침!
    setCommentInput("");
    setCommentPage(0);
    loadComments(true);
  };

  // 댓글 삭제
  const handleDeleteComment = async commentId => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    // 실제 DELETE: fetch(`/api/comments/${commentId}`, {method: "DELETE"})
    setCommentPage(0);
    loadComments(true);
  };

  // 삭제, 수정, 채팅 등 버튼
  const handleDeletePost = () => {
    if (window.confirm("정말 삭제하시겠습니까?")) {
      alert("삭제 완료 (API 연동 필요)");
      // 실제로는 삭제 후 목록으로 이동!
    }
  };

  return (
      <section style={{ backgroundColor: "#f8f9fa", padding: "2rem", borderRadius: "0.5rem" }}>
        <div className="row">
          {/* 이미지 영역 */}
          {post.imagePaths && (
              <div className="col-md-6 mb-3">
                <div className="d-flex flex-wrap gap-2">
                  {post.imagePaths.map((img, idx) => (
                      <img
                          key={idx}
                          src={img}
                          className="img-fluid rounded"
                          style={{ width: 200, height: 200, objectFit: "cover", borderRadius: 10 }}
                          alt={`img${idx}`}
                      />
                  ))}
                </div>
              </div>
          )}

          {/* 상세 정보 */}
          <div className="col-md-6">
            <h3 className="fw-bold mb-3">{post.title}</h3>
            <p>
              <span className="badge bg-secondary mb-2">{post.categoryDisplayName}</span>
            </p>
            <p className="mb-2">작성자: {post.sellerNickname}</p>
            <p className="mb-2 text-primary fs-4 fw-semibold">
              {post.price ? post.price.toLocaleString() + "원" : "가격 미정"}
            </p>
            <p className="mb-4">{post.description}</p>

            {/* 버튼 영역 */}
            <div className="d-flex gap-2">
              {!post.isAuthor && (
                  <button className="btn btn-outline-success">채팅하기</button>
              )}
              {post.isAuthor && (
                  <>
                    <a href={`/posts/edit/${post.id}`} className="btn btn-outline-primary">수정</a>
                    <button className="btn btn-danger" onClick={handleDeletePost}>삭제</button>
                  </>
              )}
            </div>
          </div>
        </div>

        {/* 댓글 섹션 */}
        <div className="mt-5">
          <h5 className="mb-3">댓글</h5>

          {/* 댓글 리스트 */}
          <div className="list-group mb-4">
            {comments.map(comment => (
                <div className="list-group-item" key={comment.id}>
                  <div className="d-flex justify-content-between align-items-center">
                    <strong>{comment.writerNickname}</strong>
                    <small className="text-muted">
                      {comment.createdAt.replace("T", " ").slice(0, 16)}
                    </small>
                  </div>
                  <p className="mb-0">{comment.content}</p>
                  {comment.writerId === currentUserId && (
                      <button
                          className="btn btn-sm btn-outline-danger mt-2"
                          onClick={() => handleDeleteComment(comment.id)}
                      >
                        삭제
                      </button>
                  )}
                </div>
            ))}
          </div>

          {/* 댓글 작성 폼 */}
          <form className="d-flex flex-column gap-2" onSubmit={handleCommentSubmit}>
            <label>
            <textarea
                className="form-control"
                rows={3}
                placeholder="댓글을 입력하세요"
                value={commentInput}
                onChange={e => setCommentInput(e.target.value)}
            />
            </label>
            <button type="submit" className="btn btn-primary align-self-end">
              댓글 작성
            </button>
          </form>
          {/* 더보기 버튼 */}
          {hasMore && (
              <button
                  className="btn btn-secondary mt-2"
                  onClick={() => loadComments(false)}
                  disabled={loading}
              >
                더보기
              </button>
          )}
        </div>
      </section>
  );
}

export default PostDetail;
