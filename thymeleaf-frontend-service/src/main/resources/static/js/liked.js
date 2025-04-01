window.addEventListener("load", () => {
    let likesBtn = document.getElementsByClassName("likes");

    for (let i = 0; i < likesBtn.length; i++) {
        if (likesBtn[i].getAttribute("value") == "true") {
            toggleLikedPost(likesBtn[i]);
        }

        likesBtn[i].addEventListener("click", function (e) {
            e.preventDefault();
        });

        likesBtn[i].addEventListener("click", toogleLikePost);
    }

    function toogleLikePost() {
        if (this.getAttribute("value") == "false") {
            like(this.name);
        } else {
            disslike(this.name);
        }
    }

    function toggleLikedPost(post) {
        post.classList.toggle("text-danger");
    }

    function like(idPost) {
        $.ajax({
            type:'POST',//тип запроса
            data:{idPost : idPost},//параметры запроса
            url : '/profile/likePost_ajax',//this is url mapping for controller
            success : function(response) {
                let obj = strToObject(response);
                setUpdatedInfo(obj);
            },
            error : function() {
                console.log("Error ajax");
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }

    function disslike(idPost) {
        $.ajax({
            type:'POST',//тип запроса
            data:{idPost : idPost},//параметры запроса
            url : '/profile/dislikePost_ajax',//this is url mapping for controller
            success : function(response) {
                let obj = strToObject(response);
                setUpdatedInfo(obj);
            },
            error : function() {
                console.log("Error ajax");
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }

    function setUpdatedInfo(obj) {
        let totalLikes = document.getElementById("like_count");
        let post = document.getElementById(obj.idPost);
        toggleLikedPost(post);
        post.setAttribute("value", obj.userIsLiked);
        post.innerHTML = "<span>" + obj.likes +"</span> <i class=\"far fa-heart\"></i>";
        totalLikes.innerHTML = obj.totalLikes;
    }
});

