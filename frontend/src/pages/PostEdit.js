// src/pages/PostEdit.js
import React, { useState } from "react";

// 샘플 카테고리 및 게시글 데이터 (실제로는 API로 받아옴)
const categories = [
  { name: "RPG", displayName: "RPG" },
  { name: "FPS", displayName: "FPS" },
  { name: "TRADING", displayName: "거래" },
];

const initialPost = {
  id: 1,
  title: "수정할 게시글 제목",
  category: "RPG",
  price: 50000,
  description: "기존에 입력한 설명입니다.",
  imagePaths: [
    { id: 101, imagePath: "https://picsum.photos/150?random=101" },
    { id: 102, imagePath: "https://picsum.photos/150?random=102" },
  ],
};

function PostEdit() {
  const [form, setForm] = useState({
    title: initialPost.title,
    category: initialPost.category,
    price: initialPost.price,
    description: initialPost.description,
    images: [],
    deletedItemImageIds: [],
  });

  // 기존 이미지 삭제 체크박스 관리
  const handleDeleteImageCheck = (imgId, checked) => {
    setForm((prev) => ({
      ...prev,
      deletedItemImageIds: checked
          ? [...prev.deletedItemImageIds, imgId]
          : prev.deletedItemImageIds.filter((id) => id !== imgId),
    }));
  };

  // 입력값 변경
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // 새 이미지 파일
  const handleFileChange = (e) => {
    setForm((prev) => ({ ...prev, images: Array.from(e.target.files) }));
  };

  // 제출 핸들러
  const handleSubmit = (e) => {
    e.preventDefault();
    // FormData 활용하여 서버에 PUT/PATCH 요청
    const data = new FormData();
    data.append("title", form.title);
    data.append("category", form.category);
    data.append("price", form.price);
    data.append("description", form.description);
    form.images.forEach((file) => data.append("images", file));
    form.deletedItemImageIds.forEach((id) =>
        data.append("deletedItemImageIds", id)
    );
    // fetch(`/api/posts/${initialPost.id}`, { method: "PUT", body: data })

    alert("수정 완료! (실제 서버 연동 필요)");
  };

  return (
      <section className="p-4 bg-light rounded" style={{ maxWidth: 600, margin: "2rem auto" }}>
        <h3 className="mb-3">게시글 수정</h3>
        <form onSubmit={handleSubmit} encType="multipart/form-data">
          <div className="mb-3">
            <label htmlFor="title" className="form-label">제목</label>
            <input
                type="text"
                className="form-control"
                id="title"
                name="title"
                value={form.title}
                onChange={handleChange}
                required
            />
            <label htmlFor="category" className="form-label mt-3">카테고리</label>
            <select
                id="category"
                name="category"
                className="form-select"
                value={form.category}
                onChange={handleChange}
                required
            >
              {categories.map((cat) => (
                  <option key={cat.name} value={cat.name}>{cat.displayName}</option>
              ))}
            </select>
          </div>
          <div className="mb-3">
            <label htmlFor="price" className="form-label">가격</label>
            <input
                type="number"
                className="form-control"
                id="price"
                name="price"
                value={form.price}
                onChange={handleChange}
                required
            />
          </div>
          <div className="mb-3">
            <label htmlFor="description" className="form-label">설명</label>
            <textarea
                className="form-control"
                id="description"
                name="description"
                rows={4}
                value={form.description}
                onChange={handleChange}
                required
            ></textarea>
          </div>

          {/* 기존 이미지 표시 및 삭제 */}
          <div className="mb-3">
            <div className="mb-2">기존 이미지</div>
            {initialPost.imagePaths.map((img) => (
                <div key={img.id} className="d-flex align-items-center mb-2">
                  <img src={img.imagePath} alt="기존 이미지" width={80} className="me-2 rounded" />
                  <label className="form-check-label me-2">
                    <input
                        type="checkbox"
                        className="form-check-input"
                        checked={form.deletedItemImageIds.includes(img.id)}
                        onChange={(e) => handleDeleteImageCheck(img.id, e.target.checked)}
                    /> 삭제
                  </label>
                </div>
            ))}
          </div>

          {/* 새 이미지 추가 */}
          <div className="mb-3">
            <label htmlFor="images" className="form-label">새 이미지 추가</label>
            <input
                type="file"
                className="form-control"
                id="images"
                name="images"
                multiple
                onChange={handleFileChange}
            />
          </div>

          <button type="submit" className="btn btn-primary">수정하기</button>
        </form>
      </section>
  );
}

export default PostEdit;
